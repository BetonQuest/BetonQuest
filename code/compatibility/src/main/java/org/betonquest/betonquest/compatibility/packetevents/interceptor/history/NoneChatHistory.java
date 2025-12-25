package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import org.bukkit.entity.Player;

/**
 * A ChatHistory implementation that does not store any chat history.
 */
public class NoneChatHistory implements ChatHistory {

    /**
     * Constructs a NoneChatHistory instance.
     */
    public NoneChatHistory() {
    }

    @Override
    public void sendHistory(final Player player) {
        // Empty
    }
}
