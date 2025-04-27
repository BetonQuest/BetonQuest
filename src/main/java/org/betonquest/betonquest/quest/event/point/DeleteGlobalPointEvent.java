package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

/**
 * Deletes a category from the global points.
 */
public class DeleteGlobalPointEvent implements NullableEvent {
    /**
     * The category to delete.
     */
    private final Variable<String> category;

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates a new DeleteGlobalPointEvent.
     *
     * @param globalData the global data
     * @param category   the category to delete
     */
    public DeleteGlobalPointEvent(final GlobalData globalData, final Variable<String> category) {
        this.category = category;
        this.globalData = globalData;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        globalData.removePointsCategory(category.getValue(profile));
    }
}
