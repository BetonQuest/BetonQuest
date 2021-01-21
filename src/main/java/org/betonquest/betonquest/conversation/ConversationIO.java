package org.betonquest.betonquest.conversation;

/**
 * Used to display messages in conversation
 */
public interface ConversationIO {

    /**
     * Set the text of response chosen by the NPC. Should be called once per
     * conversation cycle.
     *
     * @param npcName  the name of the NPC
     * @param response the text the NPC chose
     */
    void setNpcResponse(String npcName, String response);

    /**
     * Adds the text of the player option. Should be called for each option in a
     * conversation cycle.
     *
     * @param option the text of an option
     */
    void addPlayerOption(String option);

    /**
     * Displays all data to the player. Should be called after setting all
     * options.
     */
    void display();

    /**
     * Clears the data. Should be called before the cycle begins to ensure
     * nothing is left from previous one.
     */
    void clear();

    /**
     * Ends the work of this conversation IO. Should be called when the
     * conversation ends.
     */
    void end();

    /**
     * @return if this conversationIO should send messages to the player when the conversation starts and ends
     */
    default boolean printMessages() {
        return true;
    }

    /**
     * Send message through ConversationIO
     */
    default void print(final String message) {
        // Empty
    }
}
