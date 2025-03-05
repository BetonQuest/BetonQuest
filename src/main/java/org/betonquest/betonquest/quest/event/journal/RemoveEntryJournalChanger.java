package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.JournalEntryID;

/**
 * A journal changer that will remove a specified entry.
 */
public class RemoveEntryJournalChanger implements JournalChanger {

    /**
     * Entry to remove from the journal.
     */
    private final JournalEntryID entryID;

    /**
     * Create the entry-removing journal changer.
     *
     * @param entryID entry to remove
     */
    public RemoveEntryJournalChanger(final JournalEntryID entryID) {
        this.entryID = entryID;
    }

    @Override
    public void changeJournal(final Journal journal) {
        journal.removePointer(entryID);
    }
}
