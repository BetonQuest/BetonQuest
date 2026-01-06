package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.betonquest.betonquest.database.GlobalData;
import org.jetbrains.annotations.Nullable;

/**
 * Deletes a category from the global points.
 */
public class DeleteGlobalPointAction implements NullableAction {

    /**
     * The category to delete.
     */
    private final Argument<String> category;

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
    public DeleteGlobalPointAction(final GlobalData globalData, final Argument<String> category) {
        this.category = category;
        this.globalData = globalData;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        globalData.removePointsCategory(category.getValue(profile));
    }
}
