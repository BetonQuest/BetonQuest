package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

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
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ConversationIdentifier> conversationID = instruction.identifier(ConversationIdentifier.class).get();
        return new ConversationCondition(conversationApi, conversationID);
    }
}
