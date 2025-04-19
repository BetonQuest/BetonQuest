package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;

/**
 * Tellraw conversation output.
 */
public class TellrawConvIOFactory implements ConversationIORegistry.ConversationIOFactory {
    /**
     * Create a new Tellraw conversation IO factory.
     */
    public TellrawConvIOFactory() {
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        return new TellrawConvIO(conversation, onlineProfile);
    }
}
