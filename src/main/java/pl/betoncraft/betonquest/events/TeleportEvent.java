package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Teleports the player to specified location
 */
public class TeleportEvent extends QuestEvent {

    private final CompoundLocation loc;

    public TeleportEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation();
    }

    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Conversation conv = Conversation.getConversation(playerID);
        if (conv != null) {
            conv.endConversation();
        }

        final Location playerLocation = loc.getLocation(playerID);
        PlayerConverter.getPlayer(playerID).teleport(playerLocation);
        return null;
    }
}
