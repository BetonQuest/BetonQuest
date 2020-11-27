package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Forces the player to run commands.
 */
public class ChatEvent extends QuestEvent {

    private final String[] messages;

    public ChatEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        try {
            final String string = instruction.getInstruction();
            messages = string.trim().substring(string.indexOf(" ") + 1).split("\\|");
        } catch (IndexOutOfBoundsException e) {
            throw new InstructionParseException("Could not parse message", e);
        }
    }

    @Override
    protected Void execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        for (final String message : messages) {
            player.chat(message.replace("%player%", player.getName()));
        }
        return null;
    }

}
