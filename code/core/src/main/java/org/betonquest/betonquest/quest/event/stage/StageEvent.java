package org.betonquest.betonquest.quest.event.stage;

import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * The StageEvent class to make changes to a player's stage.
 */
public class StageEvent implements PlayerEvent {
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
