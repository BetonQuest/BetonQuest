package org.betonquest.betonquest.quest.event.journal;

import lombok.CustomLog;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A journal changer that will remove a specified entry.
 */
@CustomLog(topic = "RemoveEntryJournalChanger")
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
        try {
            journal.removePointer(entryName);
        } catch (final QuestRuntimeException e) {
            LOG.warn("Couldn't addPointer due to: " + e.getMessage(), e);
        }
    }
}
