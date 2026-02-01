package org.betonquest.betonquest.quest.condition.sneak;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;

/**
 * Returns true if the player is sneaking.
 */
public class SneakCondition implements OnlineCondition {

    /**
     * Create the sneak condition.
     */
    public SneakCondition() {
    }

    @Override
    public boolean check(final OnlineProfile profile) {
        return profile.getPlayer().isSneaking();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
