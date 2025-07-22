package org.betonquest.betonquest.quest.condition.flying;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

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
