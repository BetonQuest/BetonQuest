package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.NotificationSender;

/**
 * Modifies players Points.
 */
public class PointEvent implements Event {

    /**
     * The notification sender to use.
     */
    private final NotificationSender pointSender;

    /**
     * The plain name of the category.
     */
    private final String categoryName;

    /**
     * The category name.
     */
    private final String category;

    /**
     * The count.
     */
    private final VariableNumber count;

    /**
     * The point type, how the points should be modified.
     */
    private final Point pointType;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Creates a new point event.
     *
     * @param pointSender  the notification sender to use
     * @param categoryName the plain name of the category
     * @param category     the category name
     * @param count        the count
     * @param pointType    the point type
     * @param dataStorage  the storage providing player data
     */
    public PointEvent(final NotificationSender pointSender, final String categoryName, final String category, final VariableNumber count,
                      final Point pointType, final PlayerDataStorage dataStorage) {
        this.pointSender = pointSender;
        this.categoryName = categoryName;
        this.category = category;
        this.count = count;
        this.pointType = pointType;
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData playerData = dataStorage.getOffline(profile);
        final double countDouble = count.getValue(profile).doubleValue();
        playerData.setPoints(category, pointType.modify(playerData.getPointsFromCategory(category).orElse(0), countDouble));
        pointSender.sendNotification(profile, String.valueOf(countDouble), categoryName);
    }
}
