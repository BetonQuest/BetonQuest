package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Fires the conversation for the player
 */
@SuppressWarnings("PMD.CommentRequired")
public class ConversationEvent extends QuestEvent {

    private final String conv;

    public ConversationEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        conv = Utils.addPackage(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Void execute(final String playerID) {
        new Conversation(playerID, conv, PlayerConverter.getPlayer(playerID).getLocation());
        return null;
    }
}
