package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Deletes a category from the global points.
 */
public class DeleteGlobalPointEvent implements Event {
    /**
     * The category to delete.
     */
    private final String category;

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates a new DeleteGlobalPointEvent.
     *
     * @param category the category to delete
     */
    public DeleteGlobalPointEvent(final String category) {
        this.category = category;
        this.globalData = BetonQuest.getInstance().getGlobalData();
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        globalData.removePointsCategory(category);
    }
}
