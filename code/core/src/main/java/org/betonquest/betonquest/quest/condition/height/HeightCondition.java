package org.betonquest.betonquest.quest.condition.height;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * Condition to check if a player is at a certain height or lower.
 */
public class HeightCondition implements OnlineCondition {

    /**
     * The height to check for.
     */
    private final Argument<Number> height;

    /**
     * Creates a new height condition.
     *
     * @param height the height to check for
     */
    public HeightCondition(final Argument<Number> height) {
        this.height = height;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().getLocation().getY() < height.getValue(profile).doubleValue();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
