package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Forces the player to run commands.
 */
public class SudoEvent implements Event {

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

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        try {
            commands.forEach(command -> player.performCommand(command.getString(profile)));
        } catch (final RuntimeException exception) {
            throw new QuestRuntimeException("Unhandled exception executing command: " + exception.getMessage(), exception);
        }
    }
}
