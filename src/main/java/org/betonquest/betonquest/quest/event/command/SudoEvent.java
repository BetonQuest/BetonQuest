package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Forces the player to run commands.
 */
public class SudoEvent implements OnlineEvent {

    /**
     * The commands to run.
     */
    private final List<VariableString> commands;

    /**
     * Creates a new SudoEvent.
     *
     * @param commands the commands to run
     */
    public SudoEvent(final List<VariableString> commands) {
        this.commands = commands;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        try {
            for (final VariableString command : commands) {
                player.performCommand(command.getValue(profile));
            }
        } catch (final RuntimeException exception) {
            throw new QuestException("Unhandled exception executing command: " + exception.getMessage(), exception);
        }
    }
}
