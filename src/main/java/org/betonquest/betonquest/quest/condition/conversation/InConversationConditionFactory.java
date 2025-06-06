package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;

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
        final Variable<ConversationID> conversationID = instruction.getValue("conversation", ConversationID::new);
        return new InConversationCondition(conversationID);
    }
}
