package org.betonquest.betonquest.quest.condition.burning;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exception.QuestException;

/**
 * Checks if the player is burning.
 */
public class BurningCondition implements OnlineCondition {

    /**
     * Constructor of the BurningCondition.
     */
    public BurningCondition() {
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().getFireTicks() > 0;
    }
}
