package pl.betoncraft.betonquest.conversation;

/**
 * Represents the state of a conversation.
 */
public enum ConversationState {
    CREATED(false, false),
    ACTIVE(true, false),
    ENDED(true, true);

    /**
     * True if the conversation has started.
     */
    private final boolean started;

    /**
     * True if the conversation has ended.
     */
    private final boolean ended;

    ConversationState(final boolean started, final boolean ended) {

        this.started = started;
        this.ended = ended;
    }

    /**
     * Returns true if the conversation has started.
     *
     * @return true if the conversation has started.
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Returns true if the conversation is active.
     *
     * @return true if the conversation is active.
     */
    public boolean isActive() {
        return started && !ended;
    }

    /**
     * Returns true if the conversation is inactive.
     *
     * @return true if the conversation is inactive.
     */
    public boolean isInactive() {
        return !started || ended;
    }

    /**
     * Returns true if the conversation has ended.
     *
     * @return true if the conversation has ended.
     */
    public boolean isEnded() {
        return ended;
    }
}
