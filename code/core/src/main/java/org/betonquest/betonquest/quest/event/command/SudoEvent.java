package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Forces the player to run commands.
 */
public class SudoEvent implements OnlineAction {

    /**
     * The commands to run.
     */
    private final List<Argument<String>> commands;

    /**
     * Creates a new SudoEvent.
     *
     * @param commands the commands to run
     */
    public SudoEvent(final List<Argument<String>> commands) {
        this.commands = commands;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        try {
            for (final Argument<String> command : commands) {
                player.performCommand(command.getValue(profile));
            }
        } catch (final RuntimeException exception) {
            throw new QuestException("Unhandled exception executing command: " + exception.getMessage(), exception);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
