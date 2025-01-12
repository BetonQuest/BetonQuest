package org.betonquest.betonquest.quest.event.folder;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Folder event is a collection of other events, that can be run after a delay and with a periode between the event.
 * The events can be randomly chosen to run or not.
 */
public class FolderEvent implements NullableEvent {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin manager to register the quit listener.
     */
    private final PluginManager pluginManager;

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
     * The time unit to use for the delay and period.
     */
    private final TimeUnit timeUnit;

    /**
     * Whether the event should be canceled on logout.
     */
    private final boolean cancelOnLogout;

    /**
     * Conditions to check if the event should be canceled.
     */
    private final ConditionID[] cancelConditions;

    /**
     * Create a folder event with the given parameters.
     *
     * @param betonQuest       the BetonQuest instance
     * @param log              custom logger for this class
     * @param pluginManager    the plugin manager to register the quit listener
     * @param events           events to run
     * @param delay            delay to apply before running the events
     * @param period           delay to apply between each event
     * @param random           number of events to run
     * @param timeUnit         time unit to use for the delay and period
     * @param cancelOnLogout   whether the event should be canceled on logout
     * @param cancelConditions conditions to check if the event should be canceled
     */
    public FolderEvent(final BetonQuest betonQuest, final BetonQuestLogger log, final PluginManager pluginManager, final EventID[] events, @Nullable final VariableNumber delay,
                       @Nullable final VariableNumber period, @Nullable final VariableNumber random,
                       final TimeUnit timeUnit, final boolean cancelOnLogout, final ConditionID... cancelConditions) {
        this.betonQuest = betonQuest;
        this.log = log;
        this.pluginManager = pluginManager;
        this.delay = delay;
        this.period = period;
        this.random = random;
        this.events = Arrays.copyOf(events, events.length);
        this.timeUnit = timeUnit;
        this.cancelOnLogout = cancelOnLogout;
        this.cancelConditions = cancelConditions.clone();
    }

    private boolean checkCancelConditions(@Nullable final Profile profile) {
        return cancelConditions.length != 0 && BetonQuest.conditions(profile, cancelConditions);
    }

    private void executeAllEvents(@Nullable final Profile profile, final Deque<EventID> chosenList) {
        for (final EventID event : chosenList) {
            if (checkCancelConditions(profile)) {
                return;
            }
            BetonQuest.event(profile, event);
        }
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Deque<EventID> chosenList = getEventOrder(profile);
        final long delayTicks = delay == null ? 0 : timeUnit.getTicks(delay.getValue(profile).longValue());
        final long periodTicks = period == null ? 0 : timeUnit.getTicks(period.getValue(profile).longValue());
        if (delayTicks == 0 && periodTicks == 0) {
            executeAllEvents(profile, chosenList);
        } else if (periodTicks == 0) {
            handleDelayNoPeriod(profile, chosenList, delayTicks);
        } else {
            handleDelayPeriod(profile, delayTicks, chosenList, periodTicks);
        }
    }

    private void handleDelayPeriod(@Nullable final Profile profile, final long delayTicks, final Deque<EventID> chosenList, final long periodTicks) {
        if (delayTicks == 0 && !chosenList.isEmpty()) {
            final EventID event = chosenList.removeFirst();
            if (checkCancelConditions(profile)) {
                return;
            }
            BetonQuest.event(profile, event);
        }
        if (!chosenList.isEmpty()) {
            final FolderEventCanceller eventCanceller = createFolderEventCanceller(profile);
            callSameSyncAsyncContext(new BukkitRunnable() {
                @Override
                public void run() {
                    final EventID event = chosenList.pollFirst();
                    if (eventCanceller.isCancelled() || event == null || checkCancelConditions(profile)) {
                        eventCanceller.destroy();
                        this.cancel();
                        return;
                    }
                    BetonQuest.event(profile, event);
                }
            }, delayTicks == 0 ? periodTicks : delayTicks, periodTicks);
        }
    }

    private void handleDelayNoPeriod(@Nullable final Profile profile, final Deque<EventID> chosenList, final long delayTicks) {
        final FolderEventCanceller eventCanceller = createFolderEventCanceller(profile);
        callSameSyncAsyncContext(new BukkitRunnable() {
            @Override
            public void run() {
                eventCanceller.destroy();
                if (eventCanceller.isCancelled()) {
                    return;
                }
                executeAllEvents(profile, chosenList);
            }
        }, delayTicks, -1);
    }

    private Deque<EventID> getEventOrder(@Nullable final Profile profile) throws QuestException {
        final Deque<EventID> chosenList = new LinkedList<>();
        // choose randomly which events should be fired
        final int randomInt = random == null ? 0 : random.getValue(profile).intValue();
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
        return chosenList;
    }

    private FolderEventCanceller createFolderEventCanceller(@Nullable final Profile profile) {
        if (cancelOnLogout && profile != null) {
            return new QuitListener(betonQuest, log, pluginManager, profile);
        } else {
            return () -> false;
        }
    }

    private void callSameSyncAsyncContext(final BukkitRunnable runnable, final long delay, final long period) {
        if (Bukkit.getServer().isPrimaryThread()) {
            if (period == -1) {
                runnable.runTaskLater(betonQuest, delay);
            } else {
                runnable.runTaskTimer(betonQuest, delay, period);
            }
        } else {
            if (period == -1) {
                runnable.runTaskLaterAsynchronously(betonQuest, delay);
            } else {
                runnable.runTaskTimerAsynchronously(betonQuest, delay, period);
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
         * @param betonQuest    the betonquest instance
         * @param log           custom logger for this class
         * @param pluginManager the plugin manager to register the quit listener
         * @param profile       profile to check for
         */
        public QuitListener(final BetonQuest betonQuest, final BetonQuestLogger log, final PluginManager pluginManager,
                            final Profile profile) {
            this.log = log;
            this.profile = profile;
            pluginManager.registerEvents(this, betonQuest);
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
