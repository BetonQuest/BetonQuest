package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Modifies players Points
 */
public class PointEvent implements Event {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(PointEvent.class);

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
     * The path to the quest
     */
    private final QuestPackage questPackage;
    /**
     * Whether to notify the player about the change
     */
    private final boolean notify;
    /**
     * The full id of the event
     */
    private final String fullId;

    /**
     * Creates a new point event
     *
     * @param categoryName the plain name of the category
     * @param category     the category name
     * @param count        the count
     * @param pointType    the point type
     * @param questPackage the path to the quest
     * @param fullId       the full id of the event
     * @param notify       whether to notify the player about the change
     */
    public PointEvent(final String categoryName, final String category, final VariableNumber count, final Point pointType, final QuestPackage questPackage, final String fullId, final boolean notify) {
        this.categoryName = categoryName;
        this.category = category;
        this.count = count;
        this.pointType = pointType;
        this.questPackage = questPackage;
        this.fullId = fullId;
        this.notify = notify;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final PlayerData playerData = BetonQuest.getInstance().getOfflinePlayerData(profile);
        final double countDouble = count.getDouble(profile);
        playerData.setPoints(category, pointType.modify(playerData.hasPointsFromCategory(category), countDouble));
        if (notify && profile.getOnlineProfile().isPresent()) {
            try {
                Config.sendNotify(questPackage.getQuestPath(), profile.getOnlineProfile().get(), pointType.getNotifyCategory(),
                        new String[]{String.valueOf(countDouble), categoryName}, pointType.getNotifyCategory() + ",info");
            } catch (final QuestRuntimeException e) {
                LOG.warn(questPackage, "The notify system was unable to play a sound for the 'point_multiplied' category in '" + fullId + "'. Error was: '" + e.getMessage() + "'", e);
            }
        }
    }
}
