package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
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
    private final Variable<String> category;

    /**
     * The amount of points.
     */
    private final Variable<Number> count;

    /**
     * Whether the points should be equal to the specified amount.
     */
    private final boolean equal;

    /**
     * Constructor for the point condition.
     *
     * @param dataStorage the storage providing player data
     * @param category    the category of the points
     * @param count       the amount of points
     * @param equal       whether the points should be equal to the specified amount
     */
    public PointCondition(final PlayerDataStorage dataStorage, final Variable<String> category, final Variable<Number> count, final boolean equal) {
        this.dataStorage = dataStorage;
        this.category = category;
        this.count = count;
        this.equal = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Optional<Integer> amount = dataStorage.get(profile).getPointsFromCategory(category.getValue(profile));
        return amount.isPresent() && checkPoints(amount.get(), profile);
    }

    private boolean checkPoints(final int points, final Profile profile) throws QuestException {
        final int pCount = this.count.getValue(profile).intValue();
        return equal ? points == pCount : points >= pCount;
    }
}
