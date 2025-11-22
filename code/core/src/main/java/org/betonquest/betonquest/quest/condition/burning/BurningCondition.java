package org.betonquest.betonquest.quest.condition.burning;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

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
    public boolean check(final OnlineProfile profile) {
        return profile.getPlayer().getFireTicks() > 0;
    }
}
