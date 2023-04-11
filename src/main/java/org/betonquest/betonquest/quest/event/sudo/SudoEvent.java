package org.betonquest.betonquest.quest.event.sudo;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * Forces the player to run commands.
 */
public class SudoEvent implements Event {

    /**
     * The commands to run.
     */
    private final String[] commands;

    /**
     * Creates a new SudoEvent.
     *
     * @param commands the commands to run
     */
    public SudoEvent(final String... commands) {
        this.commands = commands.clone();
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        for (final String command : commands) {
            player.performCommand(command.replace("%player%", player.getName()));
        }
    }
}
