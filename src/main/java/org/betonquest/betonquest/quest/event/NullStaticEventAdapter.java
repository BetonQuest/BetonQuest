package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Adapt a normal event as a "static" event by executing it with null as player.
 *
 * @deprecated use {@link org.betonquest.betonquest.api.quest.event.HybridEvent HybridEvents}s instead of passing null
 * as NotNull annotated profile parameter
 */
@Deprecated
public class NullStaticEventAdapter implements StaticEvent {

    /**
     * The event to execute with null as playerId.
     */
    private final Event event;

    /**
     * Create a "static" event that will execute the given normal event with null for the playerId.
     *
     * @param event event to execute
     */
    public NullStaticEventAdapter(final Event event) {
        this.event = event;
    }

    @Override
    public void execute() throws QuestRuntimeException {
        event.execute(null);
    }
}
