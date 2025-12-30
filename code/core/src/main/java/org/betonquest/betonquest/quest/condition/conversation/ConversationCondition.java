package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.conversation.ConversationID;

/**
 * Checks if the conversation with player has at least one possible option.
 */
public class ConversationCondition implements PlayerCondition {

    /**
     * The Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * The conversation to check.
     */
    private final Argument<ConversationID> conversationID;

    /**
     * Creates a new ConversationCondition.
     *
     * @param conversationApi the Conversation API
     * @param conversationID  the conversation to check
     */
    public ConversationCondition(final ConversationApi conversationApi, final Argument<ConversationID> conversationID) {
        this.conversationApi = conversationApi;
        this.conversationID = conversationID;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return conversationApi.getData(conversationID.getValue(profile)).isReady(profile);
    }
}
