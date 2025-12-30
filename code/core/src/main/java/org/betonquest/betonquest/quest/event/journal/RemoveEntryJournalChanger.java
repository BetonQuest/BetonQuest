package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.JournalEntryID;

/**
 * A journal changer that will remove a specified entry.
 */
public class RemoveEntryJournalChanger implements JournalChanger {

    /**
     * The journal entry to remove.
     */
    private final Argument<JournalEntryID> entryID;

    /**
     * Create the entry-removing journal changer.
     *
     * @param entryID the journal entry to remove
     */
    public RemoveEntryJournalChanger(final Argument<JournalEntryID> entryID) {
        this.entryID = entryID;
    }

    @Override
    public void changeJournal(final Journal journal, final Profile profile) throws QuestException {
        journal.removePointer(entryID.getValue(profile));
    }
}
