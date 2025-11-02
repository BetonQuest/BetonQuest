package org.betonquest.betonquest.quest.variable.point;

import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Allows you to display total amount of global points or amount of global points remaining to
 * some other amount.
 */
public class GlobalPointVariable extends AbstractPointVariable<GlobalData> implements PlayerlessVariable {

    /**
     * Creates a new GlobalPointVariable.
     *
     * @param globalData the global data holder
     * @param category   the category of the point
     * @param amount     the number to calculate the point to
     * @param type       the type of how the points should be calculated
     */
    public GlobalPointVariable(final GlobalData globalData, final String category, final int amount, final PointCalculationType type) {
        super(globalData, category, amount, type);
    }

    @Override
    public String getValue() {
        return getValueFor(data.getPointsFromCategory(category).orElse(0));
    }
}
