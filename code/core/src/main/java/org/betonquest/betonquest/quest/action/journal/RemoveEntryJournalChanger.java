package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;

/**
 * A journal changer that will remove a specified entry.
 */
public class RemoveEntryJournalChanger implements JournalChanger {

    /**
     * Entry to remove from the journal.
     */
    private final Argument<JournalEntryIdentifier> entryID;

    /**
     * Create the entry-removing journal changer.
     *
     * @param entryID entry to remove
     */
    public RemoveEntryJournalChanger(final Argument<JournalEntryIdentifier> entryID) {
        this.entryID = entryID;
    }

    @Override
    public void changeJournal(final Journal journal, final Profile profile) throws QuestException {
        journal.removePointer(entryID.getValue(profile));
    }
}
