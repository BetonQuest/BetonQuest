package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.PointHolder;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * A condition that checks if a {@link PointHolder} has a certain amount of points.
 */
public class DefaultPointCondition implements NullableCondition {

    /**
     * Function to get the point holder from a profile.
     */
    private final Function<Profile, PointHolder> holderFunction;

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
     * Constructor for the point condition which gets the {@link PointHolder} for a given {@link Profile}.
     *
     * @param holderFunction the function to get the point holder from a profile
     * @param category       the category of the points
     * @param count          the amount of points
     * @param equal          whether the points should be equal to the specified amount
     * @param fallback       the default value when there is no value in the category at all
     */
    public DefaultPointCondition(final Function<Profile, PointHolder> holderFunction, final Argument<String> category, final Argument<Number> count,
                                 final FlagArgument<Boolean> equal, final FlagArgument<Number> fallback) {
        this.holderFunction = holderFunction;
        this.category = category;
        this.count = count;
        this.equal = equal;
        this.fallback = fallback;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Optional<Integer> amount = holderFunction.apply(profile).get(category.getValue(profile));
        if (amount.isPresent() && checkPoints(amount.get(), profile)) {
            return true;
        }
        final Optional<Number> fallback = this.fallback.getValue(profile);
        return fallback.isPresent() && checkPoints(fallback.get().intValue(), profile);
    }

    private boolean checkPoints(final int points, @Nullable final Profile profile) throws QuestException {
        final int pCount = this.count.getValue(profile).intValue();
        return equal.getValue(profile).orElse(false) ? points == pCount : points >= pCount;
    }
}
