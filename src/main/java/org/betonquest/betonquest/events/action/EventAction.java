package org.betonquest.betonquest.events.action;

/**
 * Allows executing event specific actions.
 */
public interface EventAction {
    /**
     * Run the event's action.
     *
     * @param playerId player running the event action for
     */
    void doAction(final String playerId);
}
