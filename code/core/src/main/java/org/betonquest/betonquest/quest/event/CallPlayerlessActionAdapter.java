package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * Adapter to allow executing a playerless event with the API of a player event.
 */
public class CallPlayerlessActionAdapter implements PlayerAction {

    /**
     * The playerless event to execute.
     */
    private final PlayerlessAction event;

    /**
     * Create a player event that will execute the given playerless event.
     *
     * @param event playerless event to execute
     */
    public CallPlayerlessActionAdapter(final PlayerlessAction event) {
        this.event = event;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        event.execute();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return event.isPrimaryThreadEnforced();
    }
}
