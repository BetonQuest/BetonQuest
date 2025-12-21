package org.betonquest.betonquest.quest.placeholder.name;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;

/**
 * Factory to create {@link QuesterPlaceholder}s from {@link Instruction}s.
 */
public class QuesterPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create a NpcName placeholder factory.
     *
     * @param conversationApi the Conversation API
     */
    public QuesterPlaceholderFactory(final ConversationApi conversationApi) {
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) {
        return new QuesterPlaceholder(conversationApi);
    }
}
