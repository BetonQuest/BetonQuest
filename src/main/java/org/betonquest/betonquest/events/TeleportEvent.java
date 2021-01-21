package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;

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
