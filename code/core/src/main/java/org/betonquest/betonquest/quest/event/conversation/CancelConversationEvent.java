package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.conversation.Conversation;

/**
 * Cancels the conversation.
 */
public class CancelConversationEvent implements OnlineEvent {
    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create a new conversation cancel event.
     *
     * @param conversationApi the Conversation API
     */
    public CancelConversationEvent(final ConversationApi conversationApi) {
        this.conversationApi = conversationApi;
    }

    @Override
    public void execute(final OnlineProfile profile) {
        final Conversation conv = conversationApi.getActive(profile);
        if (conv != null) {
            conv.endConversation();
        }
    }
}
