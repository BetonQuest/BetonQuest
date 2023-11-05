package org.betonquest.betonquest.quest.event.stage;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * The StageEvent class to make changes to a player's stage.
 */
public class StageEvent implements Event {
    /**
     * The action to perform.
     */
    private final StageAction action;

    /**
     * Creates the stage event.
     *
     * @param action the action to perform
     */
    public StageEvent(final StageAction action) {
        this.action = action;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        action.execute(profile);
    }

    /**
     * The stage action interface.
     */
    public interface StageAction {
        /**
         * Execute the action.
         *
         * @param profile the profile to execute the action for
         * @throws QuestRuntimeException when the action fails
         */
        void execute(Profile profile) throws QuestRuntimeException;
    }
}
