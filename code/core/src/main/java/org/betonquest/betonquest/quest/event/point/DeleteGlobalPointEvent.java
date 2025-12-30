package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.database.GlobalData;
import org.jetbrains.annotations.Nullable;

/**
 * Deletes a category from the global points.
 */
public class DeleteGlobalPointEvent implements NullableEvent {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * The category to delete.
     */
    private final Argument<String> category;

    /**
     * Creates a new DeleteGlobalPointEvent.
     *
     * @param globalData the global data
     * @param category   the category to delete
     */
    public DeleteGlobalPointEvent(final GlobalData globalData, final Argument<String> category) {
        this.globalData = globalData;
        this.category = category;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        globalData.removePointsCategory(category.getValue(profile));
    }
}
