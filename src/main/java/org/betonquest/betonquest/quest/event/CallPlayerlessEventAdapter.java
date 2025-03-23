package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;

/**
 * Adapter to allow executing a playerless event with the API of a player event.
 */
public class CallPlayerlessEventAdapter implements PlayerEvent {

    /**
     * The playerless event to execute.
     */
    private final PlayerlessEvent event;

    /**
     * Create a player event that will execute the given playerless event.
     *
     * @param event playerless event to execute
     */
    public CallPlayerlessEventAdapter(final PlayerlessEvent event) {
        this.event = event;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        event.execute();
    }
}
