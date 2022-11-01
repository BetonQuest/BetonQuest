package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.utils.Utils;

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
        final String rawConversationID = instruction.getOptional("conversation");
        this.conversationID = rawConversationID == null ? null : Utils.addPackage(instruction.getPackage(), rawConversationID);
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final Conversation conversation = Conversation.getConversation(profile);
        return conversation != null && (conversationID == null || conversation.getID().equals(conversationID));
    }
}
