package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.event.NotificationSender;

/**
 * Modifies players Points
 */
public class PointEvent implements Event {

    /**
     * The notification sender to use
     */
    private final NotificationSender pointSender;

    /**
     * The plain name of the category
     */
    private final String categoryName;

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
     * Creates a new point event
     *
     * @param pointSender  the notification sender to use
     * @param categoryName the plain name of the category
     * @param category     the category name
     * @param count        the count
     * @param pointType    the point type
     */
    public PointEvent(final NotificationSender pointSender, final String categoryName, final String category, final VariableNumber count, final Point pointType) {
        this.pointSender = pointSender;
        this.categoryName = categoryName;
        this.category = category;
        this.count = count;
        this.pointType = pointType;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final PlayerData playerData = BetonQuest.getInstance().getOfflinePlayerData(profile);
        final double countDouble = count.getDouble(profile);
        playerData.setPoints(category, pointType.modify(playerData.hasPointsFromCategory(category), countDouble));
        pointSender.sendNotification(profile, String.valueOf(countDouble), categoryName);
    }
}
