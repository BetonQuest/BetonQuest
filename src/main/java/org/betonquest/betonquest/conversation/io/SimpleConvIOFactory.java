package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Simple chat-based conversation output.
 */
public class SimpleConvIOFactory implements ConversationIOFactory {
    /**
     * Create a new Simple conversation IO factory.
     */
    public SimpleConvIOFactory() {
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        return new SimpleConvIO(conversation, onlineProfile);
    }
}
