package org.betonquest.betonquest.conversation.menu.input;

/**
 * Interface for processing control inputs.
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
