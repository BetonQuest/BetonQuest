package org.betonquest.betonquest.compatibility.packetevents.conversation.input;

/**
 * Interface for steering control inputs.
 */
public interface ConversationAction {
    /**
     * Processes the unmount input.
     */
    void unmount();

    /**
     * Processes the jump input.
     */
    void jump();

    /**
     * Processes the forward input.
     */
    void forward();

    /**
     * Processes the backwards input.
     */
    void back();

    /**
     * Processes the left input.
     */
    void left();

    /**
     * Processes the right input.
     */
    void right();
}
