package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.betonquest.betonquest.conversation.Conversation;

/**
 * Cancels the conversation.
 */
public class CancelConversationAction implements OnlineAction {

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create a new conversation cancel action.
     *
     * @param conversationApi the Conversation API
     */
    public CancelConversationAction(final ConversationApi conversationApi) {
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
