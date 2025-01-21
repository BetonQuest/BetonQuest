package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A condition that checks if global data has a certain amount of points.
 */
public class GlobalPointCondition implements NullableCondition {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * The category of the points.
     */
    private final String category;

    /**
     * The amount of points.
     */
    private final VariableNumber count;

    /**
     * Whether the points should be equal to the specified amount.
     */
    private final boolean equal;

    /**
     * Constructor for the global point condition.
     *
     * @param globalData the global data
     * @param category   the category of the points
     * @param count      the amount of points
     * @param equal      whether the points should be equal to the specified amount
     */
    public GlobalPointCondition(final GlobalData globalData, final String category, final VariableNumber count, final boolean equal) {
        this.globalData = globalData;
        this.category = category;
        this.count = count;
        this.equal = equal;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final List<Point> points = globalData.getPoints();
        for (final Point point : points) {
            if (point.getCategory().equals(category)) {
                return checkPoints(point.getCount(), profile);
            }
        }
        return false;
    }

    private boolean checkPoints(final int points, @Nullable final Profile profile) throws QuestException {
        final int pCount = this.count.getValue(profile).intValue();
        return equal ? points == pCount : points >= pCount;
    }
}
