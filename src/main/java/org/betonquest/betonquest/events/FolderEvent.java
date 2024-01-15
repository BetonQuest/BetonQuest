package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.OnlineQuestEvent;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

/**
 * Folder event is a collection of other events, that can be run after a delay and the events can be randomly chosen to
 * run or not.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class FolderEvent extends OnlineQuestEvent {
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
    private final VariableNumber delay;

    /**
     * The delay to apply between each event.
     */
    private final VariableNumber period;

    /**
     * The number of events to run.
     */
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
     * The execution mode of this folder event
     */
    private final ExecutionMode executionMode;

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
        executionMode = ExecutionMode.parse(instruction.getOptional("executionMode", "default"));
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Deque<EventID> chosenList = new LinkedList<>();
        // choose randomly which events should be fired
        final int randomInt = random == null ? 0 : random.getInt(profile);
        if (randomInt > 0 && randomInt <= events.length) {
            // copy events into the modifiable ArrayList
            final ArrayList<EventID> eventsList = new ArrayList<>(Arrays.asList(events));
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
            if (!usingEnsureMode(profile)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final FolderEventCanceller eventCanceller = createFolderEventCanceller(profile);
                        eventCanceller.destroy();
                        if (eventCanceller.isCancelled()) {
                            return;
                        }
                        for (final EventID event : chosenList) {
                            BetonQuest.event(profile, event);
                        }
                    }
                }.runTaskLater(BetonQuest.getInstance(), execDelay);
            }
        } else {
            if (!usingEnsureMode(profile)) {
                if (execDelay == null && !chosenList.isEmpty()) {
                    final EventID event = chosenList.removeFirst();
                    BetonQuest.event(profile, event);
                }
                if (!chosenList.isEmpty()) {
                    final FolderEventCanceller eventCanceller = createFolderEventCanceller(profile);
                    new BukkitRunnable() {
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
                    }.runTaskTimer(BetonQuest.getInstance(), execDelay == null ? execPeriod : execDelay, execPeriod);
                }
            }
        }
        return null;
    }

    private boolean usingEnsureMode(@NotNull final Profile profile) {
        final Long execDelay = getInTicks(delay, profile);
        if (executionMode == ExecutionMode.ENSURE_EXECUTION) {
            setNextEvent(profile, 0);
            final var canceller = createFolderEventCanceller(profile);
            if (execDelay == null) {
                ensureExecute(profile, canceller);
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ensureExecute(profile, canceller);
                    }
                }.runTaskLater(BetonQuest.getInstance(), execDelay);
            }
            return true;
        }

        return false;
    }

    /**
     * this function is used to safely execute player's events
     *
     * @param profile   the player's profile
     * @param canceller check if the execution is cancelled
     */
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    private void ensureExecute(final Profile profile, final FolderEventCanceller canceller) {
        final var nextEvent = getNextEvent(profile);
        if (nextEvent == null) {
            canceller.destroy();
            return;
        }
        if (profile.getOnlineProfile().isEmpty()) {
            canceller.destroy();
            return;
        }

        final Long execPeriod = getInTicks(period, profile);
        if (execPeriod == null) {
            canceller.destroy();
            if (canceller.isCancelled()) {
                return;
            }
            if (profile.getPlayer().isOnline() && profile.getOnlineProfile().isPresent()) {
                final var onlineProfile = profile.getOnlineProfile().get();
                for (int i = 0; i < events.length; i++) {
                    if (i < nextEvent) {
                        continue;
                    }
                    final var eventId = events[i];
                    BetonQuest.event(onlineProfile, eventId);
                    moveToNextEvent(onlineProfile, i);
                }
            }
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    canceller.destroy();
                    if (canceller.isCancelled()) {
                        this.cancel();
                        return;
                    }
                    if (profile.getPlayer().isOnline() && profile.getOnlineProfile().isPresent()) {
                        final var onlineProfile = profile.getOnlineProfile().get();
                        final var eventToExecuted = getNextEvent(profile);
                        if (eventToExecuted != null) {
                            if (eventToExecuted >= events.length) {
                                removeNextEvent(onlineProfile, eventToExecuted);
                                this.cancel();
                                return;
                            }
                            BetonQuest.event(onlineProfile, events[eventToExecuted]);
                            moveToNextEvent(onlineProfile, eventToExecuted);
                        } else {
                            this.cancel();
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(BetonQuest.getInstance(), 0, execPeriod);
        }
    }

    private FolderEventCanceller createFolderEventCanceller(final Profile profile) {
        if (executionMode == ExecutionMode.CANCEL_ON_LOGOUT) {
            return new QuitListener(log, profile);
        } else if (executionMode == ExecutionMode.ENSURE_EXECUTION) {
            return new QuitListener(log, profile);
        } else {
            return () -> false;
        }
    }

    @Nullable
    private Integer getNextEvent(@NotNull final Profile profile) {
        final var fullId = getFullId();
        for (final String tag : BetonQuest.getInstance().getPlayerData(profile).getTags()) {
            if (tag.startsWith(fullId + "#")) {
                final var eventMark = tag.substring(fullId.length());
                if (eventMark.charAt(0) == '#' && eventMark.contains("#ensuredata")) {
                    final var data = Arrays.stream(eventMark.split("#")).filter((it) -> !it.isBlank())
                            .toList();
                    final var index = Integer.parseInt(data.get(0));
                    if (index >= events.length) {
                        return null;
                    }
                    return index;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private Long getInTicks(final VariableNumber timeVariable, final Profile profile) {
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

    private void setNextEvent(@NotNull final Profile profile, final int index) {
        if (index == -1 || index >= events.length) {
            return;
        }
        final var fullId = getFullId();
        final var playerData = BetonQuest.getInstance().getPlayerData(profile);
        playerData.addTag(fullId + "#" + index + "#ensuredata");
    }

    private void removeNextEvent(@NotNull final Profile profile, final int index) {
        if (index == -1) {
            return;
        }
        final var playerData = BetonQuest.getInstance().getPlayerData(profile);
        for (final String tag : playerData.getTags()) {
            if (tag.startsWith(this.getFullId() + "#" + index)) {
                playerData.removeTag(tag);
            }
        }
    }

    private void moveToNextEvent(@NotNull final Profile profile, final int index) {
        removeNextEvent(profile, index);
        setNextEvent(profile, index + 1);
    }

    @Override
    protected String getFullId() {
        if (super.getFullId().endsWith("no-id")) {
            for (final QuestEvent event : BetonQuest.getEvents()) {
                if (event instanceof final RunEvent runEvent) {
                    final var index = runEvent.getEvents().indexOf(this);
                    if (index != -1) {
                        return runEvent.getFullId() + ".folder." + index;
                    }
                }
            }
            log.warn("folder event '" + super.getFullId() + "' cannot be found in run event, this could lead to unexpected behaviour");
        }

        return super.getFullId();
    }

    /**
     * Try to execute the folder event that has not been finished
     *
     * @param event the player join event
     */
    @Override
    public void onPlayerOnline(@NotNull final PlayerJoinEvent event) {
        if (this.executionMode == ExecutionMode.ENSURE_EXECUTION) {
            ensureExecute(PlayerConverter.getID(event.getPlayer()), createFolderEventCanceller(PlayerConverter.getID(event.getPlayer())));
        }
    }

    /**
     * The mode of the folder event
     */
    private enum ExecutionMode {
        /**
         * The default execution mode of folder
         */
        DEFAULT,
        /**
         * If the player quits during execution, the event is cancelled
         */
        CANCEL_ON_LOGOUT,
        /**
         * Will ensure every single event be executed during the player's online time
         */
        ENSURE_EXECUTION;

        public static ExecutionMode parse(@NotNull final String mode) throws InstructionParseException {
            switch (mode.toLowerCase(Locale.ROOT)) {
                case "default" -> {
                    return DEFAULT;
                }
                case "cancelonlogout" -> {
                    return CANCEL_ON_LOGOUT;
                }
                case "ensureexecution" -> {
                    return ENSURE_EXECUTION;
                }
                default ->
                        throw new InstructionParseException("There is no such " + ExecutionMode.class.getSimpleName() + ": " + mode);
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
