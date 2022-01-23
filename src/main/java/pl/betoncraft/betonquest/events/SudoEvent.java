package pl.betoncraft.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Forces the player to run commands.
 */
@SuppressWarnings("PMD.CommentRequired")
public class SudoEvent extends QuestEvent {

    private final String[] commands;

    public SudoEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.getInstruction().trim();
        int index = string.indexOf("conditions:");

        index = index == -1 ? string.length() : index;
        commands = string.substring(string.indexOf(' ') + 1, index).split("\\|");
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        for (final String command : commands) {
            player.performCommand(command.replace("%player%", player.getName()));
        }
        return null;
    }

}
