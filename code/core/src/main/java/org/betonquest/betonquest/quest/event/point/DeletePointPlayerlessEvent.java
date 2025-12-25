package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;

/**
 * Deletes the points in the category from all online players and database entries.
 */
public class DeletePointPlayerlessEvent implements PlayerlessEvent {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Database saver to use for writing offline player data.
     */
    private final Saver saver;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Point category to remove.
     */
    private final Argument<String> category;

    /**
     * Create a new Point remove event for every player, online and offline.
     *
     * @param dataStorage     the storage providing player data
     * @param saver           the saver to use
     * @param profileProvider the profile provider instance
     * @param category        the category to remove
     */
    public DeletePointPlayerlessEvent(final PlayerDataStorage dataStorage, final Saver saver, final ProfileProvider profileProvider, final Argument<String> category) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.profileProvider = profileProvider;
        this.category = category;
    }

    @Override
    public void execute() throws QuestException {
        final String category = this.category.getValue(null);
        for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
            dataStorage.get(onlineProfile).removePointsCategory(category);
        }
        saver.add(new Saver.Record(UpdateType.REMOVE_ALL_POINTS, category));
    }
}
