package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

/**
 * Teleports the player to specified location
 */
@SuppressWarnings("PMD.CommentRequired")
public class TeleportEvent extends QuestEvent {

    private final CompoundLocation loc;

    public TeleportEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation();
    }

    @Override
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
