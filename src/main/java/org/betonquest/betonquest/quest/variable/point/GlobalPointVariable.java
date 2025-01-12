package org.betonquest.betonquest.quest.variable.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;

/**
 * Allows you to display total amount of global points or amount of global points remaining to
 * some other amount.
 */
public class GlobalPointVariable extends AbstractPointVariable implements PlayerlessVariable {

    /**
     * Creates a new GlobalPointVariable.
     *
     * @param betonQuest the BetonQuest instance
     * @param category   the category of the point
     * @param amount     the number to calculate the point to
     * @param type       the type of how the points should be calculated
     */
    public GlobalPointVariable(final BetonQuest betonQuest, final String category, final int amount, final PointCalculationType type) {
        super(betonQuest, category, amount, type);
    }

    @Override
    public String getValue() {
        return getValueFor(betonQuest.getGlobalData().getPointsFromCategory(category).orElse(0));
    }
}
