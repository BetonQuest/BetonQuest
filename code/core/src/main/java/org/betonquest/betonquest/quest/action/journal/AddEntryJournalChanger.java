package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;

import java.time.InstantSource;

/**
 * A journal changer that will add a specified entry.
 */
public class AddEntryJournalChanger implements JournalChanger {

    /**
     * Instant source for new journal entries.
     */
    private final InstantSource instantSource;

    /**
     * Entry to add to the journal.
     */
    private final Argument<JournalEntryIdentifier> entryID;

    /**
     * Create the entry-adding journal changer.
     *
     * @param instantSource source to get the journal entry date from
     * @param entryID       entry to add
     */
    public AddEntryJournalChanger(final InstantSource instantSource, final Argument<JournalEntryIdentifier> entryID) {
        this.instantSource = instantSource;
        this.entryID = entryID;
    }

    @Override
    public void changeJournal(final Journal journal, final Profile profile) throws QuestException {
        journal.addPointer(new Pointer(entryID.getValue(profile), instantSource.millis()));
    }
}
