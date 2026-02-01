package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.database.GlobalData;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
    private final Argument<String> category;

    /**
     * The amount of points.
     */
    private final Argument<Number> count;

    /**
     * Whether the points should be equal to the specified amount.
     */
    private final FlagArgument<Boolean> equal;

    /**
     * Constructor for the global point condition.
     *
     * @param globalData the global data
     * @param category   the category of the points
     * @param count      the amount of points
     * @param equal      whether the points should be equal to the specified amount
     */
    public GlobalPointCondition(final GlobalData globalData, final Argument<String> category, final Argument<Number> count, final FlagArgument<Boolean> equal) {
        this.globalData = globalData;
        this.category = category;
        this.count = count;
        this.equal = equal;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Optional<Integer> point = globalData.getPointsFromCategory(category.getValue(profile));
        return point.isPresent() && checkPoints(point.get(), profile);
    }

    private boolean checkPoints(final int point, @Nullable final Profile profile) throws QuestException {
        final int pCount = this.count.getValue(profile).intValue();
        return equal.getValue(profile).orElse(false) ? point == pCount : point >= pCount;
    }
}
