package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link RemoveEntryJournalChanger}.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
class RemoveEntryJournalChangerTest {
    @Test
    void testChangeJournalRemovesPointer(@Mock final Journal journal) throws QuestException {
        final JournalEntryID entryID = mock(JournalEntryID.class);
        final RemoveEntryJournalChanger changer = new RemoveEntryJournalChanger(new Variable<>(entryID));

        changer.changeJournal(journal, mock(Profile.class));

        verify(journal).removePointer(entryID);
        verifyNoMoreInteractions(journal);
    }
}
