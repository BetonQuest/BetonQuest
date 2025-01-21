package org.betonquest.betonquest.quest.condition.sneak;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

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
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().isSneaking();
    }
}
