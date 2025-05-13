package org.betonquest.betonquest.quest.event.point;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.event.NotificationSender;

/**
 * Modifies players Points.
 */
public class PointEvent implements PlayerEvent {

    /**
     * The notification sender to use.
     */
    private final NotificationSender pointSender;

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
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Creates a new point event.
     *
     * @param pointSender the notification sender to use
     * @param category    the category name
     * @param count       the count
     * @param pointType   the point type
     * @param dataStorage the storage providing player data
     */
    public PointEvent(final NotificationSender pointSender, final Variable<String> category, final Variable<Number> count,
                      final Point pointType, final PlayerDataStorage dataStorage) {
        this.pointSender = pointSender;
        this.category = category;
        this.count = count;
        this.pointType = pointType;
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData playerData = dataStorage.getOffline(profile);
        final double countDouble = count.getValue(profile).doubleValue();
        final String category = this.category.getValue(profile);
        playerData.setPoints(category, pointType.modify(playerData.getPointsFromCategory(category).orElse(0), countDouble));
        pointSender.sendNotification(profile,
                new VariableReplacement("amount", Component.text(countDouble)),
                new VariableReplacement("category", Component.text(category)));
    }
}
