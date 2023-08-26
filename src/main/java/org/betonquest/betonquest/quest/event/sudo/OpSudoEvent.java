package org.betonquest.betonquest.quest.event.sudo;

import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Forces the player to run commands.
 */
public class OpSudoEvent implements Event {

    /**
     * The commands to run.
     */
    private final List<VariableString> commands;

    /**
     * Creates a new SudoEvent.
     *
     * @param commands the commands to run
     */
    public OpSudoEvent(final List<VariableString> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            commands.forEach(command -> player.performCommand(command.getString(profile)));
        } finally {
            player.setOp(previousOp);
        }
    }
}
