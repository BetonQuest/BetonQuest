package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * Adapter to allow executing a playerless action with the API of a player action.
 */
public class CallPlayerlessActionAdapter implements PlayerAction {

    /**
     * The playerless action to execute.
     */
    private final PlayerlessAction action;

    /**
     * Create a player action that will execute the given playerless action.
     *
     * @param action playerless action to execute
     */
    public CallPlayerlessActionAdapter(final PlayerlessAction action) {
        this.action = action;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        action.execute();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return action.isPrimaryThreadEnforced();
    }
}
