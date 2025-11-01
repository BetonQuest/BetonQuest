package org.betonquest.betonquest.conversation.interceptor;

import net.kyori.adventure.text.Component;

/**
 * The interceptor is used to intercept chat messages that are sent to the player.
 * This is useful to provide a distraction-free conversation experience.
 */
public interface Interceptor {

    /**
     * Starts the work of this interceptor.
     */
    void begin();

    /**
     * Send a message to player bypassing Interceptor.
     *
     * @param message the message
     */
    void sendMessage(Component message);

    /**
     * Ends the work of this interceptor.
     */
    void end();
}
