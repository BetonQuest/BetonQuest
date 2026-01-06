package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;

/**
 * A journal changer that will not change anything.
 */
public class NoOperationJournalChanger implements JournalChanger {

    /**
     * Create the no operation journal changer.
     */
    public NoOperationJournalChanger() {
    }

    @Override
    public void changeJournal(final Journal journal, final Profile profile) {
        // null object pattern
    }
}
