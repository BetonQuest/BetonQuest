package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.feature.journal.Journal;

/**
 * Defines changes to be done to a journal.
 */
public interface JournalChanger {
    /**
     * Apply the change to a journal.
     *
     * @param journal journal to change
     */
    void changeJournal(Journal journal);
}
