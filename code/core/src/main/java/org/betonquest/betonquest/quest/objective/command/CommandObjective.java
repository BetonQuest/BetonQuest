package org.betonquest.betonquest.quest.objective.command;

import org.apache.commons.lang3.Strings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

/**
 * Requires the player to execute a specific command.
 */
public class CommandObjective extends DefaultObjective {

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
     * Actions to trigger if the command is not matched.
     */
    private final Argument<List<ActionID>> failActions;

    /**
     * Creates a new instance of the CommandObjective.
     *
     * @param service     the objective factory service
     * @param command     the command that the player has to execute
     * @param ignoreCase  whether the command should ignore the capitalization
     * @param exact       whether the command should be matched exactly or just the start
     * @param cancel      whether the command should be cancelled after matching
     * @param failActions actions to trigger if the command is not matched
     * @throws QuestException if there is an error in the instruction
     */
    public CommandObjective(final ObjectiveFactoryService service, final Argument<String> command,
                            final FlagArgument<Boolean> ignoreCase, final FlagArgument<Boolean> exact,
                            final FlagArgument<Boolean> cancel, final Argument<List<ActionID>> failActions) throws QuestException {
        super(service);
        this.command = command;
        this.ignoreCase = ignoreCase;
        this.exact = exact;
        this.cancel = cancel;
        this.failActions = failActions;
    }

    /**
     * Checks if the command matches the one that was executed.
     *
     * @param event         the event that was triggered
     * @param onlineProfile the profile of the player that executed the command
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onCommand(final PlayerCommandPreprocessEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final String replaceCommand = command.getValue(onlineProfile);
        if (foundMatch(onlineProfile, event.getMessage(), replaceCommand)) {
            if (cancel.getValue(onlineProfile).orElse(false)) {
                event.setCancelled(true);
            }
            getService().complete(onlineProfile);
            return;
        }
        BetonQuest.getInstance().getQuestTypeApi().actions(onlineProfile, failActions.getValue(onlineProfile));
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
