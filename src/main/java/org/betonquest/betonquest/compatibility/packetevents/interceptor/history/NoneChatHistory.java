package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import net.kyori.adventure.text.Component;
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

    @Override
    public Component addBypass(final Component component) {
        return component;
    }
}
