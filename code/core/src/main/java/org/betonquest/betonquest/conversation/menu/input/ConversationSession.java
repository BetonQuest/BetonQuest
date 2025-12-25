package org.betonquest.betonquest.conversation.menu.input;

/**
 * Handles the player session while in the conversation.
 */
public interface ConversationSession {

    /**
     * Adds the conversation modifications to the player.
     */
    void begin();

    /**
     * Removes the conversation modifications.
     */
    void end();
}
