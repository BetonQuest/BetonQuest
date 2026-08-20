package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * A condition that checks if a player has a certain point set.
 */
public class HasPointCondition implements PlayerCondition {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The category of the points.
     */
    private final Argument<String> category;

    /**
     * Constructor for the has point condition.
     *
     * @param dataStorage the storage providing player data
     * @param category    the category of the points
     */
    public HasPointCondition(final PlayerDataStorage dataStorage, final Argument<String> category) {
        this.dataStorage = dataStorage;
        this.category = category;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return dataStorage.get(profile).points().get(category.getValue(profile)).isPresent();
    }
}
