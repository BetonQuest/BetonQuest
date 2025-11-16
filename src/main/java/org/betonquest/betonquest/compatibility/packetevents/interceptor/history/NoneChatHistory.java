package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import org.betonquest.betonquest.api.common.component.tagger.ComponentTagger;
import org.betonquest.betonquest.api.common.component.tagger.NoneComponentTagger;
import org.bukkit.entity.Player;

/**
 * A ChatHistory implementation that does not store any chat history.
 */
public class NoneChatHistory implements ChatHistory {
    /**
     * A prefix that marks messages to be ignored by this history.
     */
    private static final ComponentTagger TAGGER = new NoneComponentTagger();

    /**
     * Constructs a NoneChatHistory instance.
     */
    public NoneChatHistory() {
    }

    @Override
    public void sendHistory(final Player player) {
        // Empty
    }

    @Override
    public ComponentTagger getTagger() {
        return TAGGER;
    }
}
