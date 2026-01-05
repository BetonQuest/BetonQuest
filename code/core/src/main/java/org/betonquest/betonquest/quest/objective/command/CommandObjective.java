package org.betonquest.betonquest.quest.objective.command;

import org.apache.commons.lang3.Strings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

/**
 * Requires the player to execute a specific command.
 */
public class CommandObjective extends DefaultObjective implements Listener {

    /**
     * Command that the player has to execute.
     */
    private final Argument<String> command;

    /**
     * Whether the command should ignore the capitalization.
     */
    private final FlagArgument<Boolean> ignoreCase;

    /**
     * Whether the command should be matched exactly or just the start.
     */
    private final FlagArgument<Boolean> exact;

    /**
     * Whether the command should be cancelled after matching.
     */
    private final FlagArgument<Boolean> cancel;

    /**
     * Events to trigger if the command is not matched.
     */
    private final Argument<List<ActionID>> failEvents;

    /**
     * Creates a new instance of the CommandObjective.
     *
     * @param instruction the instruction that created this objective
     * @param command     the command that the player has to execute
     * @param ignoreCase  whether the command should ignore the capitalization
     * @param exact       whether the command should be matched exactly or just the start
     * @param cancel      whether the command should be cancelled after matching
     * @param failEvents  events to trigger if the command is not matched
     * @throws QuestException if there is an error in the instruction
     */
    public CommandObjective(final Instruction instruction, final Argument<String> command,
                            final FlagArgument<Boolean> ignoreCase, final FlagArgument<Boolean> exact,
                            final FlagArgument<Boolean> cancel, final Argument<List<ActionID>> failEvents) throws QuestException {
        super(instruction);
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
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            qeHandler.handle(() -> {
                final String replaceCommand = command.getValue(onlineProfile);
                if (foundMatch(onlineProfile, event.getMessage(), replaceCommand)) {
                    if (cancel.getValue(onlineProfile).orElse(false)) {
                        event.setCancelled(true);
                    }
                    completeObjective(onlineProfile);
                } else {
                    BetonQuest.getInstance().getQuestTypeApi().events(onlineProfile, failEvents.getValue(onlineProfile));
                }
            });
        }
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }

    private boolean foundMatch(final OnlineProfile onlineProfile, final String commandExecuted, final String commandRequired) throws QuestException {
        if (exact.getValue(onlineProfile).orElse(false)) {
            return ignoreCase.getValue(onlineProfile).orElse(false) ? Strings.CI.equals(commandExecuted, commandRequired)
                    : Strings.CS.equals(commandExecuted, commandRequired);
        }
        return ignoreCase.getValue(onlineProfile).orElse(false) ? Strings.CI.startsWith(commandExecuted, commandRequired)
                : Strings.CS.startsWith(commandExecuted, commandRequired);
    }
}
