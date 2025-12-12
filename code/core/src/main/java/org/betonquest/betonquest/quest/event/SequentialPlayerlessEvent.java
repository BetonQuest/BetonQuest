package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;

import java.util.Arrays;

/**
 * A static event that is composed of other static events executed in sequence. If an error occurs execution is stopped
 * at that point.
 */
public class SequentialPlayerlessEvent implements PlayerlessEvent {

    /**
     * Events to be executed.
     */
    private final PlayerlessEvent[] playerlessEvents;

    /**
     * Create a static event sequence. The events at the front of the array will be executed first, at the end will be
     * executed last.
     *
     * @param playerlessEvents events to be executed
     */
    public SequentialPlayerlessEvent(final PlayerlessEvent... playerlessEvents) {
        this.playerlessEvents = Arrays.copyOf(playerlessEvents, playerlessEvents.length);
    }

    @Override
    public void execute() throws QuestException {
        for (final PlayerlessEvent playerlessEvent : playerlessEvents) {
            playerlessEvent.execute();
        }
    }
}
