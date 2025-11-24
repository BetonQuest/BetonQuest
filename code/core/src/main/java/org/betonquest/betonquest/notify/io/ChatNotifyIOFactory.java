package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link ChatNotifyIO}s.
 */
public class ChatNotifyIOFactory implements NotifyIOFactory {
    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create a new Chat Notify IO.
     *
     * @param conversationApi the Conversation API
     */
    public ChatNotifyIOFactory(final ConversationApi conversationApi) {
        this.conversationApi = conversationApi;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new ChatNotifyIO(pack, categoryData, conversationApi);
    }
}
