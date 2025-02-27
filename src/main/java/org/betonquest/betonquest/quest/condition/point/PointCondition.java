package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
    private final VariableIdentifier category;

    /**
     * The amount of points.
     */
    private final VariableNumber count;

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
    public PointCondition(final PlayerDataStorage dataStorage, final VariableIdentifier category, final VariableNumber count, final boolean equal) {
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
