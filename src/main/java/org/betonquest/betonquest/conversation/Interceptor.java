package org.betonquest.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * The interceptor is used to intercept chat messages that are sent to the player.
 * This is useful to provide a distraction free conversation experience.
 */
public interface Interceptor {

    /**
     * Send message to player bypassing Interceptor
     *
     * @param message the message
     */
    void sendMessage(String message);

    /**
     * Send message to player bypassing Interceptor
     *
     * @param message the message
     */
    void sendMessage(BaseComponent... message);

    /**
     * Ends the work of this interceptor
     */
    void end();
}
