package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import org.betonquest.betonquest.api.common.component.tagger.ComponentTagger;
import org.bukkit.entity.Player;

/**
 * Interface for managing chat history for players.
 */
public interface ChatHistory {
    /**
     * Sends the chat history to the specified player.
     * It tries the best to mimic the original sending as closely as possible.
     *
     * @param player the player to send the chat history to
     */
    void sendHistory(Player player);

    /**
     * Get the component tagger used to mark messages that should bypass the history.
     *
     * @return the component tagger
     */
    ComponentTagger getTagger();
}
