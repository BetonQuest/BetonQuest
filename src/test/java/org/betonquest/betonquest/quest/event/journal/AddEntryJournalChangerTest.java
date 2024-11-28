package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Pointer;
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
@ExtendWith(MockitoExtension.class)
class AddEntryJournalChangerTest {
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testChangeJournalAddsPointer(@Mock final Journal journal) {
        final Instant now = Instant.now();
        final String entryName = "test_entry";
        final AddEntryJournalChanger changer = new AddEntryJournalChanger(InstantSource.fixed(now), entryName);
        final ArgumentCaptor<Pointer> captor = ArgumentCaptor.forClass(Pointer.class);

        changer.changeJournal(journal);

        verify(journal).addPointer(captor.capture());
        verifyNoMoreInteractions(journal);
        final Pointer pointer = captor.getValue();
        assertEquals(entryName, pointer.getPointer(), "The added entry should be the one provided.");
        assertEquals(now.toEpochMilli(), pointer.getTimestamp(), "The added entry should be dated to the time from the InstantSource.");
    }
}
