package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.Optional;

/**
 * Modifies global Points
 */
public class GlobalPointEvent implements Event {

    /**
     * The global data
     */
    private final GlobalData globalData;

    /**
     * The category name
     */
    private final String category;
    /**
     * The count
     */
    private final VariableNumber count;

    /**
     * The point type, how the points should be modified
     */
    private final Point pointType;

    /**
     * Creates a new global point event
     *
     * @param category  the category name
     * @param count     the count
     * @param pointType the point type
     */
    public GlobalPointEvent(final String category, final VariableNumber count, final Point pointType) {
        this.globalData = BetonQuest.getInstance().getGlobalData();
        this.category = category;
        this.count = count;
        this.pointType = pointType;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Optional<org.betonquest.betonquest.Point> globalPoint = globalData.getPoints().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .findFirst();
        globalData.setPoints(category, pointType.modify(
                globalPoint.map(org.betonquest.betonquest.Point::getCount).orElse(0), count.getInt(profile)));
    }
}
