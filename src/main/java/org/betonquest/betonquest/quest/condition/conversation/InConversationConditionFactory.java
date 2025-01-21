package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory for {@link InConversationCondition}s.
 */
public class InConversationConditionFactory implements PlayerConditionFactory {

    /**
     * Create the in conversation factory.
     */
    public InConversationConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String rawConversationID = instruction.getOptional("conversation");
        final ConversationID conversationID;
        if (rawConversationID == null) {
            conversationID = null;
        } else {
            try {
                conversationID = new ConversationID(instruction.getPackage(), rawConversationID);
            } catch (final ObjectNotFoundException e) {
                throw new QuestException(e.getMessage(), e);
            }
        }
        return new InConversationCondition(conversationID);
    }
}
