package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.feature.journal.Journal;

/**
 * A journal changer that will remove a specified entry.
 */
public class RemoveEntryJournalChanger implements JournalChanger {

    /**
     * Entry to remove from the journal.
     */
    private final String entryName;

    /**
     * Create the entry-removing journal changer.
     *
     * @param entryName entry to remove
     */
    public RemoveEntryJournalChanger(final String entryName) {
        this.entryName = entryName;
    }

    @Override
    public void changeJournal(final Journal journal) {
        journal.removePointer(entryName);
    }
}
