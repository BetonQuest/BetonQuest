package org.betonquest.betonquest.quest.event.stage;

import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * The StageEvent class to make changes to a player's stage.
 */
public class StageEvent implements Event {
    /**
     * The action to perform.
     */
    private final QuestConsumer<Profile> action;

    /**
     * Creates the stage event.
     *
     * @param action the stage action to perform
     */
    public StageEvent(final QuestConsumer<Profile> action) {
        this.action = action;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        action.accept(profile);
    }
}
