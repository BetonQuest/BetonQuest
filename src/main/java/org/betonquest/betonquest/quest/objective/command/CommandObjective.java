package org.betonquest.betonquest.quest.objective.command;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Requires the player to execute a specific command.
 */
public class CommandObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Command that the player has to execute.
     */
    private final VariableString command;

    /**
     * Whether the command should ignore the capitalization.
     */
    private final boolean ignoreCase;

    /**
     * Whether the command should be matched exactly or just the start.
     */
    private final boolean exact;

    /**
     * Whether the command should be cancelled after matching.
     */
    private final boolean cancel;

    /**
     * Events to trigger if the command is not matched.
     */
    private final VariableList<EventID> failEvents;

    /**
     * Creates a new instance of the CommandObjective.
     *
     * @param instruction the instruction that created this objective
     * @param log         the logger for this objective
     * @param command     the command that the player has to execute
     * @param ignoreCase  whether the command should ignore the capitalization
     * @param exact       whether the command should be matched exactly or just the start
     * @param cancel      whether the command should be cancelled after matching
     * @param failEvents  events to trigger if the command is not matched
     * @throws QuestException if there is an error in the instruction
     */
    public CommandObjective(final Instruction instruction, final BetonQuestLogger log, final VariableString command,
                            final boolean ignoreCase, final boolean exact, final boolean cancel,
                            final VariableList<EventID> failEvents) throws QuestException {
        super(instruction);
        this.log = log;
        this.command = command;
        this.ignoreCase = ignoreCase;
        this.exact = exact;
        this.cancel = cancel;
        this.failEvents = failEvents;
    }

    /**
     * Checks if the command matches the one that was executed.
     *
     * @param event the event that was triggered
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            final String replaceCommand = command.getString(onlineProfile);
            if (foundMatch(event.getMessage(), replaceCommand)) {
                if (cancel) {
                    event.setCancelled(true);
                }
                completeObjective(onlineProfile);
            } else {
                try {
                    for (final EventID failEvent : failEvents.getValue(onlineProfile)) {
                        BetonQuest.getInstance().getQuestTypeAPI().event(onlineProfile, failEvent);
                    }
                } catch (final QuestException e) {
                    log.warn(instruction.getPackage(), "Failed to resolve events for command objective: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }

    private boolean foundMatch(final String commandExecuted, final String commandRequired) {
        if (exact) {
            return ignoreCase ? StringUtils.equalsIgnoreCase(commandExecuted, commandRequired)
                    : StringUtils.equals(commandExecuted, commandRequired);
        } else {
            return ignoreCase ? StringUtils.startsWithIgnoreCase(commandExecuted, commandRequired)
                    : StringUtils.startsWith(commandExecuted, commandRequired);
        }
    }
}
