package org.betonquest.betonquest.quest.event.sudo;

import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Forces the player to run commands.
 */
public class OpSudoEvent implements Event {

    /**
     * The commands to run.
     */
    private final VariableString[] commands;

    /**
     * Creates a new SudoEvent.
     *
     * @param commands the commands to run
     */
    public OpSudoEvent(final VariableString... commands) {
        this.commands = Arrays.copyOf(commands, commands.length);
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            for (final VariableString variableCommand : commands) {
                player.performCommand(variableCommand.getString(profile));
            }
        } finally {
            player.setOp(previousOp);
        }
    }
}
