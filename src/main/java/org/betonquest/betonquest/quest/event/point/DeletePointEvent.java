package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.jetbrains.annotations.Nullable;

/**
 * Deletes all points of a category.
 */
public class DeletePointEvent implements ComposedEvent {

    /**
     * The category to delete.
     */
    private final String category;

    /**
     * Creates a new DeletePointsEvent.
     *
     * @param category the category to delete
     */
    public DeletePointEvent(final String category) {
        this.category = category;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final BetonQuest betonQuest = BetonQuest.getInstance();
        if (profile == null) {
            PlayerConverter.getOnlineProfiles().forEach(onlineProfile -> betonQuest.getPlayerData(onlineProfile).removePointsCategory(category));
            betonQuest.getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_POINTS, category));
        } else {
            betonQuest.getOfflinePlayerData(profile).removePointsCategory(category);
        }
    }
}
