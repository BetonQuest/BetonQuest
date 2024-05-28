package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Folder event is a collection of other events, that can be run after a delay, and the events can be randomly chosen to
 * run or not.
 */
public class FolderEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(this.getClass());

    /**
     * Random generator used to choose events to run.
     */
    private final Random randomGenerator = new Random();

    /**
     * The delay to apply before running the events.
     */
    @Nullable
    private final VariableNumber delay;

    /**
     * The delay to apply between each event.
     */
    @Nullable
    private final VariableNumber period;

    /**
     * The number of events to run.
     */
    @Nullable
    private final VariableNumber random;

    /**
     * The events to run.
     */
    private final EventID[] events;

    /**
     * Whether the delay and period are in ticks.
     */
    private final boolean ticks;

    /**
     * Whether the delay and period are in minutes.
     */
    private final boolean minutes;

    /**
     * Whether the event should be cancelled on logout.
     */
    private final boolean cancelOnLogout;

    /**
     * The constructor called by BetonQuest via reflection.
     *
     * @param instruction the instruction to parse
     * @throws InstructionParseException if the instruction is invalid
     */
    public FolderEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        events = instruction.getList(instruction::getEvent).toArray(new EventID[0]);
        delay = instruction.getVarNum(instruction.getOptional("delay"));
        period = instruction.getVarNum(instruction.getOptional("period"));
        random = instruction.getVarNum(instruction.getOptional("random"));
        ticks = instruction.hasArgument("ticks");
        minutes = instruction.hasArgument("minutes");
        cancelOnLogout = instruction.hasArgument("cancelOnLogout");
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final Deque<EventID> chosenList = new LinkedList<>();
        // choose randomly which events should be fired
        final int randomInt = random == null ? 0 : random.getInt(profile);
        if (randomInt > 0 && randomInt <= events.length) {
            // copy events into the modifiable ArrayList
            final List<EventID> eventsList = new ArrayList<>(Arrays.asList(events));
            // remove chosen events from that ArrayList and place them in a new
            // list
            for (int i = randomInt; i > 0; i--) {
                final int chosen = randomGenerator.nextInt(eventsList.size());
                chosenList.add(eventsList.remove(chosen));
            }
        } else {
            chosenList.addAll(Arrays.asList(events));
        }

        final Long execDelay = getInTicks(delay, profile);
        final Long execPeriod = getInTicks(period, profile);

        if (execDelay == null && execPeriod == null) {
            for (final EventID event : chosenList) {
                BetonQuest.event(profile, event);
            }
        } else if (execPeriod == null) {
            final FolderEventCanceller eventCanceller = createFolderEventCanceller(profile);
            callSameSyncAsyncContext(new BukkitRunnable() {
                @Override
                public void run() {
                    eventCanceller.destroy();
                    if (eventCanceller.isCancelled()) {
                        return;
                    }
                    for (final EventID event : chosenList) {
                        BetonQuest.event(profile, event);
                    }
                }
            }, execDelay, -1);
        } else {
            if (execDelay == null && !chosenList.isEmpty()) {
                final EventID event = chosenList.removeFirst();
                BetonQuest.event(profile, event);
            }
            if (!chosenList.isEmpty()) {
                final FolderEventCanceller eventCanceller = createFolderEventCanceller(profile);
                callSameSyncAsyncContext(new BukkitRunnable() {
                    @Override
                    public void run() {
                        final EventID event = chosenList.pollFirst();
                        if (eventCanceller.isCancelled() || event == null) {
                            eventCanceller.destroy();
                            this.cancel();
                            return;
                        }
                        BetonQuest.event(profile, event);
                    }
                }, execDelay == null ? execPeriod : execDelay, execPeriod);
            }
        }
        return null;
    }

    private FolderEventCanceller createFolderEventCanceller(@Nullable final Profile profile) {
        if (cancelOnLogout && profile != null) {
            return new QuitListener(log, profile);
        } else {
            return () -> false;
        }
    }

    @Nullable
    private Long getInTicks(@Nullable final VariableNumber timeVariable, @Nullable final Profile profile) {
        if (timeVariable == null) {
            return null;
        }

        long time = timeVariable.getInt(profile);
        if (time == 0) {
            return null;
        }

        if (minutes) {
            time *= 20 * 60;
        } else if (!ticks) {
            time *= 20;
        }
        return time;
    }

    private void callSameSyncAsyncContext(final BukkitRunnable runnable, final long delay, final long period) {
        final BetonQuest instance = BetonQuest.getInstance();
        if (Bukkit.getServer().isPrimaryThread()) {
            if (period == -1) {
                runnable.runTaskLater(instance, delay);
            } else {
                runnable.runTaskTimer(instance, delay, period);
            }
        } else {
            if (period == -1) {
                runnable.runTaskLaterAsynchronously(instance, delay);
            } else {
                runnable.runTaskTimerAsynchronously(instance, delay, period);
            }
        }
    }

    /**
     * Interface to check if an execution of a folder event is cancelled.
     */
    private interface FolderEventCanceller {
        /**
         * Whether the execution of the folder event should be cancelled.
         *
         * @return true if the event needs to be cancelled; false otherwise
         */
        boolean isCancelled();

        /**
         * Clean up any resources used by the canceller if necessary.
         */
        default void destroy() {
            // Empty
        }
    }

    /**
     * Registers the quit listener if the event should be cancelled on logout.
     */
    private static class QuitListener implements FolderEventCanceller, Listener {
        /**
         * Custom {@link BetonQuestLogger} instance for this class.
         */
        private final BetonQuestLogger log;

        /**
         * The profile of the player to check for.
         */
        private final Profile profile;

        /**
         * Whether the event is cancelled.
         */
        private boolean cancelled;

        /**
         * Create a quit listener for the given profile's player.
         *
         * @param log     logger for debug messages
         * @param profile profile to check for
         */
        public QuitListener(final BetonQuestLogger log, final Profile profile) {
            this.log = log;
            this.profile = profile;
            BetonQuest.getInstance().getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        /**
         * Handle quit events to check if an execution of the folder event needs to be cancelled.
         *
         * @param event player quit event to handle
         */
        @EventHandler
        public void onPlayerQuit(final PlayerQuitEvent event) {
            if (event.getPlayer().getUniqueId().equals(profile.getPlayerUUID())) {
                cancelled = true;
                log.debug("Folder event cancelled due to disconnect of " + profile);
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void destroy() {
            PlayerQuitEvent.getHandlerList().unregister(this);
        }
    }
}
