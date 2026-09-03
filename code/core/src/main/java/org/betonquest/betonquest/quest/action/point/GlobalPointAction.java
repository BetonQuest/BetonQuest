package org.betonquest.betonquest.quest.action.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.database.GlobalData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Modifies global Points.
 */
public class GlobalPointAction implements NullableAction {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * The category name.
     */
    private final Argument<String> category;

    /**
     * The count.
     */
    private final Argument<Number> count;

    /**
     * The point type, how the points should be modified.
     */
    private final Argument<PointType> pointType;

    /**
     * Creates a new global point action.
     *
     * @param globalData the global data
     * @param category   the category name
     * @param count      the count
     * @param pointType  the point type
     */
    public GlobalPointAction(final GlobalData globalData, final Argument<String> category, final Argument<Number> count, final Argument<PointType> pointType) {
        this.globalData = globalData;
        this.category = category;
        this.count = count;
        this.pointType = pointType;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final String category = this.category.getValue(profile);
        final Optional<Map.Entry<String, Integer>> globalPoint = globalData.points().get().entrySet().stream()
                .filter(p -> p.getKey().equalsIgnoreCase(category))
                .findFirst();
        final PointType pointTypeValue = pointType.getValue(profile);
        globalData.points().set(category, pointTypeValue.modify(
                globalPoint.map(Map.Entry::getValue).orElse(0), count.getValue(profile).doubleValue()));
    }
}
