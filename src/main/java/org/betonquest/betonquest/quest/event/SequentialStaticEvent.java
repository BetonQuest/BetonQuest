package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Arrays;

/**
 * A static event that is composed of other static events executed in sequence. If an error occurs execution is stopped
 * at that point.
 */
public class SequentialStaticEvent implements StaticEvent {

    /**
     * Events to be executed.
     */
    private final StaticEvent[] staticEvents;

    /**
     * Create a static event sequence. The events at the front of the array will be executed first, at the end will be
     * executed last.
     *
     * @param staticEvents events to be executed
     */
    public SequentialStaticEvent(final StaticEvent... staticEvents) {
        this.staticEvents = Arrays.copyOf(staticEvents, staticEvents.length);
    }

    @Override
    public void execute() throws QuestException {
        for (final StaticEvent staticEvent : staticEvents) {
            staticEvent.execute();
        }
    }
}
