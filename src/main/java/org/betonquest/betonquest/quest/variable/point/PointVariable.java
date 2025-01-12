package org.betonquest.betonquest.quest.variable.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;

/**
 * Allows you to display total amount of points or amount of points remaining to
 * some other amount.
 */
public class PointVariable extends AbstractPointVariable implements PlayerVariable {
    /**
     * Creates a new PointVariable.
     *
     * @param betonQuest the BetonQuest instance
     * @param category   the category of the point
     * @param amount     the number to calculate the point to
     * @param type       the type of how the points should be calculated
     */
    public PointVariable(final BetonQuest betonQuest, final String category, final int amount, final PointCalculationType type) {
        super(betonQuest, category, amount, type);
    }

    @SuppressWarnings("PMD.TooFewBranchesForSwitch")
    @Override
    public String getValue(final Profile profile) {
        return getValueFor(betonQuest.getPlayerData(profile).getPointsFromCategory(category).orElse(0));
    }
}
