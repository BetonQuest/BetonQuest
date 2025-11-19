package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.conversation.ConversationID;

/**
 * Factory for {@link InConversationCondition}s.
 */
public class InConversationConditionFactory implements PlayerConditionFactory {
    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create the in conversation factory.
     *
     * @param conversationApi the Conversation API
     */
    public InConversationConditionFactory(final ConversationApi conversationApi) {
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<ConversationID> conversationID = instruction.getValue("conversation", ConversationID::new);
        return new InConversationCondition(conversationApi, conversationID);
    }
}
