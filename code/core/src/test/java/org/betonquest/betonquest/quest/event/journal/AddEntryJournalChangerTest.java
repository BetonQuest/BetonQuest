package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.lib.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.InstantSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link AddEntryJournalChanger}.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
class AddEntryJournalChangerTest {

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testChangeJournalAddsPointer(@Mock final Journal journal) throws QuestException {
        final Instant now = Instant.now();
        final JournalEntryID entryID = mock(JournalEntryID.class);
        final AddEntryJournalChanger changer = new AddEntryJournalChanger(InstantSource.fixed(now), new DefaultVariable<>(entryID));
        final ArgumentCaptor<Pointer> captor = ArgumentCaptor.forClass(Pointer.class);

        changer.changeJournal(journal, mock(Profile.class));

        verify(journal).addPointer(captor.capture());
        verifyNoMoreInteractions(journal);
        final Pointer pointer = captor.getValue();
        assertEquals(entryID, pointer.pointer(), "The added entry should be the one provided.");
        assertEquals(now.toEpochMilli(), pointer.timestamp(), "The added entry should be dated to the time from the InstantSource.");
    }
}
