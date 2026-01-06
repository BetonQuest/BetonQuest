package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link NoOperationJournalChanger}.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
class NoOperationJournalChangerTest {

    @Test
    void testChangeJournalDoesNothing(@Mock final Journal journal) {
        final NoOperationJournalChanger changer = new NoOperationJournalChanger();
        changer.changeJournal(journal, mock(Profile.class));
        verifyNoInteractions(journal);
    }
}
