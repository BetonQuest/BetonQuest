package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;

/**
 * Adapter to allow executing a "static" event with the API of a normal event.
 */
public class CallStaticEventAdapter implements PlayerEvent {

    /**
     * The "static" event to execute.
     */
    private final PlayerlessEvent event;

    /**
     * Create a normal event that will execute the given "static" event.
     *
     * @param event "static" event to execute
     */
    public CallStaticEventAdapter(final PlayerlessEvent event) {
        this.event = event;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        event.execute();
    }
}
