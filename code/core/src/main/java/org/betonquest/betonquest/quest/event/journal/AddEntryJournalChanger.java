package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.betonquest.betonquest.id.JournalEntryID;

import java.time.InstantSource;

/**
 * A journal changer that will add a specified entry.
 */
public class AddEntryJournalChanger implements JournalChanger {

    /**
     * The source to get the journal entry date from.
     */
    private final InstantSource instantSource;

    /**
     * The entry to add to the journal.
     */
    private final Argument<JournalEntryID> entryID;

    /**
     * Create the entry-adding journal changer.
     *
     * @param instantSource the source to get the journal entry date from
     * @param entryID       the entry to add to the journal
     */
    public AddEntryJournalChanger(final InstantSource instantSource, final Argument<JournalEntryID> entryID) {
        this.instantSource = instantSource;
        this.entryID = entryID;
    }

    @Override
    public void changeJournal(final Journal journal, final Profile profile) throws QuestException {
        journal.addPointer(new Pointer(entryID.getValue(profile), instantSource.millis()));
    }
}
