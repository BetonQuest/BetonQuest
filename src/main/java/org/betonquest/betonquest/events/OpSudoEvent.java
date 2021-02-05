package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public class OpSudoEvent extends QuestEvent {

    private final String[] commands;

    public OpSudoEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.getInstruction().trim();
        commands = string.substring(string.indexOf(' ') + 1).split("\\|");
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            for (final String command : commands) {
                player.performCommand(command.replace("%player%", player.getName()));
            }
        } finally {
            player.setOp(previousOp);
        }
        return null;
    }

}
