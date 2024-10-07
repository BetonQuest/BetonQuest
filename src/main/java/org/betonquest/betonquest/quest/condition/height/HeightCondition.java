package org.betonquest.betonquest.quest.condition.height;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Condition to check if a player is at a certain height or lower.
 */
public class HeightCondition implements OnlineCondition {

    /**
     * The height to check for.
     */
    @Nullable
    private final VariableNumber height;

    /**
     * The height in a location to check.
     */
    @Nullable
    private final VariableLocation location;

    /**
     * Creates a new height condition.
     *
     * @param height   the height to check for
     * @param location the height in a location to check
     */
    public HeightCondition(@Nullable final VariableNumber height, @Nullable final VariableLocation location) {
        this.height = height;
        this.location = location;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        final double heightValue;
        if (height != null) {
            heightValue = height.getValue(profile).doubleValue();
        } else if (location != null) {
            heightValue = location.getValue(profile).getY();
        } else {
            throw new QuestRuntimeException("Height condition must have a height or location");
        }
        return profile.getPlayer().getLocation().getY() < heightValue;
    }
}
