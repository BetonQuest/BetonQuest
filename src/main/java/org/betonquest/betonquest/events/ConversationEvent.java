package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;

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
