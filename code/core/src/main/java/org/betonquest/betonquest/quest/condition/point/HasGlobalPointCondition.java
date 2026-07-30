package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.database.GlobalData;
import org.jetbrains.annotations.Nullable;

/**
 * A condition that checks if global data has a certain point set.
 */
public class HasGlobalPointCondition implements NullableCondition {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * The category of the points.
     */
    private final Argument<String> category;

    /**
     * Constructor for the has global point condition.
     *
     * @param globalData the global data
     * @param category   the category of the points
     */
    public HasGlobalPointCondition(final GlobalData globalData, final Argument<String> category) {
        this.globalData = globalData;
        this.category = category;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return globalData.points().get(category.getValue(profile)).isPresent();
    }
}
