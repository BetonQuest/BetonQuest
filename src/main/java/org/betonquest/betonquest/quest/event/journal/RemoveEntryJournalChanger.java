package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * A journal changer that will remove a specified entry.
 */
public class RemoveEntryJournalChanger implements JournalChanger {

    /**
     * Entry to remove from the journal.
     */
    private final Variable<JournalEntryID> entryID;

    /**
     * Create the entry-removing journal changer.
     *
     * @param entryID entry to remove
     */
    public RemoveEntryJournalChanger(final Variable<JournalEntryID> entryID) {
        this.entryID = entryID;
    }

    @Override
    public void changeJournal(final Journal journal, final Profile profile) throws QuestException {
        journal.removePointer(entryID.getValue(profile));
    }
}
