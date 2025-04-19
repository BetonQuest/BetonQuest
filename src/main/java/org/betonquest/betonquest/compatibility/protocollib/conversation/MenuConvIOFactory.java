package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;

/**
 * Menu conversation output.
 */
public class MenuConvIOFactory implements ConversationIORegistry.ConversationIOFactory {
    /**
     * Create a new Menu conversation IO factory.
     */
    public MenuConvIOFactory() {
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        return new MenuConvIO(conversation, onlineProfile);
    }
}
