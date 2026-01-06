package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;

/**
 * Defines changes to be done to a journal.
 */
@FunctionalInterface
public interface JournalChanger {

    /**
     * Apply the change to a journal.
     *
     * @param journal journal to change
     * @param profile the profile to resolve placeholders for
     * @throws QuestException when an exception occurs
     */
    void changeJournal(Journal journal, Profile profile) throws QuestException;
}
