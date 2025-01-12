package org.betonquest.betonquest.quest.condition.flying;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Checks if the player is gliding with elytra.
 */
public class FlyingCondition implements OnlineCondition {

    /**
     * Create a new flying condition.
     */
    public FlyingCondition() {
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().isGliding();
    }
}
