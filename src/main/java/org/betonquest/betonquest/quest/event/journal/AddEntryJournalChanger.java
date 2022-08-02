package org.betonquest.betonquest.quest.event.journal;

import lombok.CustomLog;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.time.InstantSource;

/**
 * A journal changer that will add a specified entry.
 */
@CustomLog(topic = "AddEntryJournalChanger")
public class AddEntryJournalChanger implements JournalChanger {

    /**
     * Instant source for new journal entries.
     */
    private final InstantSource instantSource;

    /**
     * Entry to add to the journal.
     */
    private final String entryName;

    /**
     * Create the entry-adding journal changer.
     *
     * @param instantSource source to get the journal entry date from
     * @param entryName     entry to add
     */
    public AddEntryJournalChanger(final InstantSource instantSource, final String entryName) {
        this.instantSource = instantSource;
        this.entryName = entryName;
    }

    @Override
    public void changeJournal(final Journal journal) {
        try {
            journal.addPointer(new Pointer(entryName, instantSource.millis()));
        } catch (final QuestRuntimeException e) {
            LOG.warn("Couldn't addPointer due to: " + e.getMessage(), e);
        }
    }
}
