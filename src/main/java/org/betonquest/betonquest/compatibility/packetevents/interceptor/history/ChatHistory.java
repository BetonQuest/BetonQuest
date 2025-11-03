package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Interface for managing chat history for players.
 */
public interface ChatHistory {
    /**
     * Sends the chat history to the specified player.
     * If the history is smaller than the cache size, it fills the rest with new lines.
     *
     * @param player the player to send the chat history to
     */
    void sendHistory(Player player);

    /**
     * Adds a bypass tag to the given component to prevent it from being recorded in chat history.
     *
     * @param component the component to tag
     * @return the tagged component
     */
    Component addBypass(Component component);
}
