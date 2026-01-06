package org.betonquest.betonquest.quest.action.stage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * The StageAction class to make changes to a player's stage.
 */
public class StageAction implements PlayerAction {

    /**
     * The action to perform.
     */
    private final QuestConsumer<Profile> action;

    /**
     * Creates the stage action.
     *
     * @param action the stage action to perform
     */
    public StageAction(final QuestConsumer<Profile> action) {
        this.action = action;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        action.accept(profile);
    }
}
