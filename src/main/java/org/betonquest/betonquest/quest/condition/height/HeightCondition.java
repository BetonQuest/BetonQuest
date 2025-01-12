package org.betonquest.betonquest.quest.condition.height;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Condition to check if a player is at a certain height or lower.
 */
public class HeightCondition implements OnlineCondition {

    /**
     * The height to check for.
     */
    private final Variable<Number> height;

    /**
     * Creates a new height condition.
     *
     * @param height the height to check for
     */
    public HeightCondition(final Variable<Number> height) {
        this.height = height;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().getLocation().getY() < height.getValue(profile).doubleValue();
    }
}
