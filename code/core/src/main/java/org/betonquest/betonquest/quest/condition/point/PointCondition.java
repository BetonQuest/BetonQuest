package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;

import java.util.Optional;

/**
 * A condition that checks if a player has a certain amount of points.
 */
public class PointCondition implements PlayerCondition {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

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
     * Constructor for the point condition.
     *
     * @param dataStorage the storage providing player data
     * @param category    the category of the points
     * @param count       the amount of points
     * @param equal       whether the points should be equal to the specified amount
     * @param fallback    the default value when there is no value in the category at all
     */
    public PointCondition(final PlayerDataStorage dataStorage, final Argument<String> category, final Argument<Number> count,
                          final FlagArgument<Boolean> equal, final FlagArgument<Number> fallback) {
        this.dataStorage = dataStorage;
        this.category = category;
        this.count = count;
        this.equal = equal;
        this.fallback = fallback;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Optional<Integer> amount = dataStorage.getOffline(profile).points().get(category.getValue(profile));
        if (amount.isPresent() && checkPoints(amount.get(), profile)) {
            return true;
        }
        final Optional<Number> fallback = this.fallback.getValue(profile);
        return fallback.isPresent() && checkPoints(fallback.get().intValue(), profile);
    }

    private boolean checkPoints(final int points, final Profile profile) throws QuestException {
        final int pCount = this.count.getValue(profile).intValue();
        return equal.getValue(profile).orElse(false) ? points == pCount : points >= pCount;
    }
}
