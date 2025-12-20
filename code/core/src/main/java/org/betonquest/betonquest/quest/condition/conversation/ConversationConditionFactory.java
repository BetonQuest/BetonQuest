package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.conversation.ConversationID;

/**
 * A factory for creating ConversationCondition objects.
 */
public class ConversationConditionFactory implements PlayerConditionFactory {

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Creates a new ConversationConditionFactory.
     *
     * @param conversationApi the Conversation API
     */
    public ConversationConditionFactory(final ConversationApi conversationApi) {
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<ConversationID> conversationID = instruction.get(ConversationID::new);
        return new ConversationCondition(conversationApi, conversationID);
    }
}
