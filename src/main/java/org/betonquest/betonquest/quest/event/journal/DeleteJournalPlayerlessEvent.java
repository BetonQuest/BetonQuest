package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.JournalEntryID;

/**
 * Deletes the journal entry from all online players and database entries.
 */
public class DeleteJournalPlayerlessEvent implements PlayerlessEvent {
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
     * Plugin Message instance to create the journal.
     */
    private final PluginMessage pluginMessage;

    /**
     * Point category to remove.
     */
    private final Variable<JournalEntryID> entryID;

    /**
     * Create a new Journal remove event for every player, online and offline.
     *
     * @param dataStorage     the storage providing player data
     * @param saver           the saver to use
     * @param profileProvider the profile provider instance
     * @param pluginMessage   the plugin message to generate a new journal
     * @param entryID         the entry to remove
     */
    public DeleteJournalPlayerlessEvent(final PlayerDataStorage dataStorage, final Saver saver, final ProfileProvider profileProvider,
                                        final PluginMessage pluginMessage, final Variable<JournalEntryID> entryID) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.profileProvider = profileProvider;
        this.pluginMessage = pluginMessage;
        this.entryID = entryID;
    }

    @Override
    public void execute() throws QuestException {
        final JournalEntryID resolved = this.entryID.getValue(null);
        for (final OnlineProfile profile : profileProvider.getOnlineProfiles()) {
            final PlayerData playerData = dataStorage.getOffline(profile);
            final Journal journal = playerData.getJournal(pluginMessage);
            journal.removePointer(resolved);
            journal.update();
        }
        saver.add(new Saver.Record(UpdateType.REMOVE_ALL_ENTRIES, resolved.getFull()));
    }
}
