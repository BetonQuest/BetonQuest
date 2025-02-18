package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.instruction.variable.VariableString;

import java.util.function.Function;

/**
 * Deletes all points of a category.
 */
public class DeletePointEvent implements Event {
    /**
     * Function to get the player data for a profile.
     */
    private final Function<Profile, PlayerData> playerDataSource;

    /**
     * The category to delete.
     */
    private final VariableString category;

    /**
     * Creates a new DeletePointsEvent.
     *
     * @param playerDataSource the source to get a profiles player data
     * @param category         the category to delete
     */
    public DeletePointEvent(final Function<Profile, PlayerData> playerDataSource, final VariableString category) {
        this.playerDataSource = playerDataSource;
        this.category = category;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        playerDataSource.apply(profile).removePointsCategory(category.getValue(profile));
    }
}
