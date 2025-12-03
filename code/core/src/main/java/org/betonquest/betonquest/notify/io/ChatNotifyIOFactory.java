package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link ChatNotifyIO}s.
 */
public class ChatNotifyIOFactory implements NotifyIOFactory {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create a new Chat Notify IO.
     *
     * @param variables       the variable processor to create and resolve variables
     * @param conversationApi the Conversation API
     */
    public ChatNotifyIOFactory(final Variables variables, final ConversationApi conversationApi) {
        this.variables = variables;
        this.conversationApi = conversationApi;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new ChatNotifyIO(variables, pack, categoryData, conversationApi);
    }
}
