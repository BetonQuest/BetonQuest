package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;

/**
 * Adapter to allow executing a "static" event with the API of a normal event.
 */
public class CallStaticEventAdapter implements Event {

    /**
     * The "static" event to execute.
     */
    private final StaticEvent event;

    /**
     * Create a normal event that will execute the given "static" event.
     *
     * @param event "static" event to execute
     */
    public CallStaticEventAdapter(final StaticEvent event) {
        this.event = event;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        event.execute();
    }
}
