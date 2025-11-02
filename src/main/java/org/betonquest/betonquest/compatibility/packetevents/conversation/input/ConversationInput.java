package org.betonquest.betonquest.compatibility.packetevents.conversation.input;

/**
 * Handles the player state while in the conversation.
 */
public interface ConversationInput {
    /**
     * Adds the conversation modifications to the player.
     */
    void begin();

    /**
     * Removes the conversation modifications.
     */
    void end();
}
