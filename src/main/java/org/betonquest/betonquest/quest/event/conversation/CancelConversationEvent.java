package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Cancels the conversation.
 */
public class CancelConversationEvent implements OnlineEvent {

    /**
     * Create a new conversation cancel event.
     */
    public CancelConversationEvent() {
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestRuntimeException {
        final Conversation conv = Conversation.getConversation(profile);
        if (conv != null) {
            conv.endConversation();
        }
    }
}
