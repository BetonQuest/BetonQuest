package org.betonquest.betonquest.quest.event.journal;

import lombok.CustomLog;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link RemoveEntryJournalChanger}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
@CustomLog(topic = "RemoveEntryJournalChangerTest")
class RemoveEntryJournalChangerTest {
    @Test
    void testChangeJournalRemovesPointer(@Mock final Journal journal) {
        final String entryName = "test_entry";
        final RemoveEntryJournalChanger changer = new RemoveEntryJournalChanger(entryName);

        changer.changeJournal(journal);

        try {
            verify(journal).removePointer(entryName);
        } catch (final QuestRuntimeException e) {
            LOG.warn("Couldn't removePointer due to: " + e.getMessage(), e);
        }
        verifyNoMoreInteractions(journal);
    }
}
