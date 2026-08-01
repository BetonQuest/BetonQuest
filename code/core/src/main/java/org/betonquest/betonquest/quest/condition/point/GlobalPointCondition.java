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
     * Default value when there is no value for the category at all.
     */
    private final FlagArgument<Number> fallback;

    /**
     * Constructor for the global point condition.
     *
     * @param globalData the global data
     * @param category   the category of the points
     * @param count      the amount of points
     * @param equal      whether the points should be equal to the specified amount
     * @param fallback   the default value when there is no value in the category at all
     */
    public GlobalPointCondition(final GlobalData globalData, final Argument<String> category, final Argument<Number> count,
                                final FlagArgument<Boolean> equal, final FlagArgument<Number> fallback) {
        this.globalData = globalData;
        this.category = category;
        this.count = count;
        this.equal = equal;
        this.fallback = fallback;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Optional<Integer> point = globalData.points().get(category.getValue(profile));
        if (point.isPresent() && checkPoints(point.get(), profile)) {
            return true;
        }
        final Optional<Number> fallback = this.fallback.getValue(profile);
        return fallback.isPresent() && checkPoints(fallback.get().intValue(), profile);
    }

    private boolean checkPoints(final int point, @Nullable final Profile profile) throws QuestException {
        final int pCount = this.count.getValue(profile).intValue();
        return equal.getValue(profile).orElse(false) ? point == pCount : point >= pCount;
    }
}
