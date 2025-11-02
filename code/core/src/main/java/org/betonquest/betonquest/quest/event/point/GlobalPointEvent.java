package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.database.GlobalData;
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
    private final Variable<String> category;

    /**
     * The count.
     */
    private final Variable<Number> count;

    /**
     * The point type, how the points should be modified.
     */
    private final Point pointType;

    /**
     * Creates a new global point event.
     *
     * @param globalData the global data
     * @param category   the category name
     * @param count      the count
     * @param pointType  the point type
     */
    public GlobalPointEvent(final GlobalData globalData, final Variable<String> category, final Variable<Number> count, final Point pointType) {
        this.globalData = globalData;
        this.category = category;
        this.count = count;
        this.pointType = pointType;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final String category = this.category.getValue(profile);
        final Optional<org.betonquest.betonquest.Point> globalPoint = globalData.getPoints().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .findFirst();
        globalData.setPoints(category, pointType.modify(
                globalPoint.map(org.betonquest.betonquest.Point::getCount).orElse(0), count.getValue(profile).doubleValue()));
    }
}
