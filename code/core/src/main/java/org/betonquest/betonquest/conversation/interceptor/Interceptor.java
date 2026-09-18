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

    /**
     * Transfers intercepted messages and listener state to the next interceptor without flushing messages to the player,
     * if the next interceptor is compatible. Otherwise, cleans up and flushes messages.
     *
     * @param next the interceptor of the next conversation
     */
    default void transferTo(final Interceptor next) {
        end();
    }
}
