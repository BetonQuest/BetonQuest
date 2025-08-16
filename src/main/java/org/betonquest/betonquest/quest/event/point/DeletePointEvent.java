package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.database.PlayerData;

import java.util.function.Function;

/**
 * Deletes all points of a category.
 */
public class DeletePointEvent implements PlayerEvent {
    /**
     * Function to get the player data for a profile.
     */
    private final Function<Profile, PlayerData> playerDataSource;

    /**
     * The category to delete.
     */
    private final Variable<String> category;

    /**
     * Creates a new DeletePointsEvent.
     *
     * @param playerDataSource the source to get a profiles player data
     * @param category         the category to delete
     */
    public DeletePointEvent(final Function<Profile, PlayerData> playerDataSource, final Variable<String> category) {
        this.playerDataSource = playerDataSource;
        this.category = category;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        playerDataSource.apply(profile).removePointsCategory(category.getValue(profile));
    }
}
