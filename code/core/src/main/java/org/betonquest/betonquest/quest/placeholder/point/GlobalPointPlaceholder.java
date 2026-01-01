package org.betonquest.betonquest.quest.placeholder.point;

import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Allows you to display total amount of global points or amount of global points remaining to
 * some other amount.
 */
public class GlobalPointPlaceholder extends AbstractPointPlaceholder<GlobalData> implements PlayerlessPlaceholder {

    /**
     * Creates a new GlobalPointPlaceholder.
     *
     * @param globalData the global data holder
     * @param category   the category of the point
     * @param amount     the number to calculate the point to
     * @param type       the type of how the points should be calculated
     */
    public GlobalPointPlaceholder(final GlobalData globalData, final String category, final int amount, final PointCalculationType type) {
        super(globalData, category, amount, type);
    }

    @Override
    public String getValue() {
        return getValueFor(data.getPointsFromCategory(category).orElse(0));
    }
}
