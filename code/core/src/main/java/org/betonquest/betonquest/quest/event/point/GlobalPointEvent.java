package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.Point;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Modifies global Points.
 */
public class GlobalPointEvent implements NullableEvent {

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
    private final PointType pointType;

    /**
     * Creates a new global point event.
     *
     * @param globalData the global data
     * @param category   the category name
     * @param count      the count
     * @param pointType  the point type, how the points should be modified
     */
    public GlobalPointEvent(final GlobalData globalData, final Argument<String> category, final Argument<Number> count, final PointType pointType) {
        this.globalData = globalData;
        this.category = category;
        this.count = count;
        this.pointType = pointType;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final String category = this.category.getValue(profile);
        final Optional<Point> globalPoint = globalData.getPoints().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .findFirst();
        globalData.setPoints(category, pointType.modify(
                globalPoint.map(Point::getCount).orElse(0), count.getValue(profile).doubleValue()));
    }
}
