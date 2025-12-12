package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationID;
import org.jetbrains.annotations.Nullable;

/**
 * Condition to check if a player is in a conversation or, if specified, in the specified conversation.
 */
public class InConversationCondition implements PlayerCondition {
    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Identifier of the conversation.
     */
    @Nullable
    private final Variable<ConversationID> conversationID;

    /**
     * Constructor of the InConversationCondition.
     *
     * @param conversationApi the Conversation API
     * @param conversationID  the conversation identifier
     */
    public InConversationCondition(final ConversationApi conversationApi, @Nullable final Variable<ConversationID> conversationID) {
        this.conversationApi = conversationApi;
        this.conversationID = conversationID;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Conversation conversation = conversationApi.getActive(profile);
        return conversation != null && (conversationID == null || conversation.getID().equals(conversationID.getValue(profile)));
    }
}
