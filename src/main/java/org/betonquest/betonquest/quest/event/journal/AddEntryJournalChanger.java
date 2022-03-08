package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Pointer;

import java.util.Date;

/**
 * A journal changer that will add a specified entry.
 */
public class AddEntryJournalChanger implements JournalChanger {

    /**
     * Entry to add to the journal.
     */
    private final String entryName;

    /**
     * Create the entry-adding journal changer.
     *
     * @param entryName entry to add
     */
    public AddEntryJournalChanger(final String entryName) {
        this.entryName = entryName;
    }

    @Override
    public void changeJournal(final Journal journal) {
        journal.addPointer(new Pointer(entryName, new Date().getTime()));
    }
}
