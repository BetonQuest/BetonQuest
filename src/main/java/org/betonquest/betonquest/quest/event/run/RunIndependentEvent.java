package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.CallStaticEventAdapter;

import java.util.List;

/**
 * Runs specified events player independently.
 * <p>
 * Although the implementation is a {@link StaticEvent}, using it in a static context does not make much sense.
 * Recommended usage is to wrap it in a {@link CallStaticEventAdapter} and using it to call static events
 * from non-static context.
 */
public class RunIndependentEvent implements StaticEvent {

    /**
     * List of Events to run.
     */
    private final List<EventID> events;

    /**
     * Create a new RunIndependentEvent instance.
     *
     * @param events the events to run
     */
    public RunIndependentEvent(final List<EventID> events) {
        this.events = events;
    }

    @Override
    public void execute() throws QuestException {
        for (final EventID event : events) {
            BetonQuest.event(null, event);
        }
    }
}
