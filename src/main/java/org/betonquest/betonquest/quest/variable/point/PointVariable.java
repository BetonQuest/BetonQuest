package org.betonquest.betonquest.quest.variable.point;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Allows you to display total amount of points or amount of points remaining to
 * some other amount.
 */
public class PointVariable extends AbstractPointVariable<PlayerDataStorage> implements PlayerVariable {

    /**
     * Creates a new PointVariable.
     *
     * @param playerData the data holder
     * @param category   the category of the point
     * @param amount     the number to calculate the point to
     * @param type       the type of how the points should be calculated
     */
    public PointVariable(final PlayerDataStorage playerData, final String category, final int amount, final PointCalculationType type) {
        super(playerData, category, amount, type);
    }

    @Override
    public String getValue(final Profile profile) {
        return getValueFor(data.get(profile).getPointsFromCategory(category).orElse(0));
    }
}
