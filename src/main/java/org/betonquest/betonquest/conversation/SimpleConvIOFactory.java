package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;

/**
 * Simple chat-based conversation output.
 */
public class SimpleConvIOFactory implements ConversationIORegistry.ConversationIOFactory {
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
