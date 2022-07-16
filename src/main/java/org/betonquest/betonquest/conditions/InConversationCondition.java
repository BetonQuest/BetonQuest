package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if the player is in a conversation or, if specified, in the specified conversation
 */
public class InConversationCondition extends Condition {

    /**
     * Identifier of the conversation
     */
    private final String conversationID;

    /**
     * Constructor of the InConversationCondition
     *
     * @param instruction the instruction
     */
    public InConversationCondition(final Instruction instruction) {
        super(instruction, false);
        this.conversationID = instruction.getOptional("conversation");
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Conversation conversation = Conversation.getConversation(playerID);
        if (conversation != null) {
            return conversationID == null || conversation.getID().equals(conversationID);
        }
        return false;
    }
}
