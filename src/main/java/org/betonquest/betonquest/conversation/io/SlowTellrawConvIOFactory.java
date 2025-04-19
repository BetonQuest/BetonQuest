package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;

/**
 * SlowTellraw conversation output.
 */
public class SlowTellrawConvIOFactory implements ConversationIORegistry.ConversationIOFactory {
    /**
     * Create a new SlowTellraw conversation IO factory.
     */
    public SlowTellrawConvIOFactory() {
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        return new SlowTellrawConvIO(conversation, onlineProfile);
    }
}
