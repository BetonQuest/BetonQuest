package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

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
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        final int points = globalData.hasPointsFromCategory(category);
        final int pCount = count.getValue(profile).intValue();
        return equal ? points == pCount : points >= pCount;
    }
}
