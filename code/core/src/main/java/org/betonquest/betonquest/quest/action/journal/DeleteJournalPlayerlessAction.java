package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.JournalEntryID;

/**
 * Deletes the journal entry from all online players and database entries.
 */
public class DeleteJournalPlayerlessAction implements PlayerlessAction {

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
    private final Argument<JournalEntryID> entryID;

    /**
     * Create a new Journal remove action for every player, online and offline.
     *
     * @param dataStorage     the storage providing player data
     * @param saver           the saver to use
     * @param profileProvider the profile provider instance
     * @param entryID         the entry to remove
     */
    public DeleteJournalPlayerlessAction(final PlayerDataStorage dataStorage, final Saver saver, final ProfileProvider profileProvider,
                                         final Argument<JournalEntryID> entryID) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.profileProvider = profileProvider;
        this.entryID = entryID;
    }

    @Override
    public void execute() throws QuestException {
        final JournalEntryID resolved = this.entryID.getValue(null);
        for (final OnlineProfile profile : profileProvider.getOnlineProfiles()) {
            final PlayerData playerData = dataStorage.getOffline(profile);
            final Journal journal = playerData.getJournal();
            journal.removePointer(resolved);
            journal.update();
        }
        saver.add(new Saver.Record(UpdateType.REMOVE_ALL_ENTRIES, resolved.getFull()));
    }
}
