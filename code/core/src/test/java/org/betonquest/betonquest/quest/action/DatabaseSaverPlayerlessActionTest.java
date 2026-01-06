package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test {@link DatabaseSaverPlayerlessAction}.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseSaverPlayerlessActionTest {

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testAddRecord(@Mock final Saver saver) throws QuestException {
        final Saver.Record record = new Saver.Record(UpdateType.DELETE_GLOBAL_TAGS);
        final DatabaseSaverPlayerlessAction event = new DatabaseSaverPlayerlessAction(saver, () -> record);

        verify(saver, never()).add(record);
        event.execute();
        verify(saver).add(record);
        verifyNoMoreInteractions(saver);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testRecordSupplierIsCalledEveryTime(@Mock final Saver saver) throws QuestException {
        final Saver.Record firstRecord = new Saver.Record(UpdateType.DELETE_GLOBAL_TAGS);
        final Saver.Record secondRecord = new Saver.Record(UpdateType.DELETE_GLOBAL_POINTS);
        final Iterator<Saver.Record> recordsForSupplier = List.of(firstRecord, secondRecord).iterator();
        final DatabaseSaverPlayerlessAction event = new DatabaseSaverPlayerlessAction(saver, recordsForSupplier::next);

        event.execute();
        verify(saver).add(firstRecord);
        verify(saver, never()).add(secondRecord);
        event.execute();
        // saver.add(firstRecord) was executed once during the first call,
        // but it would be more than once if the second call did execute it too
        verify(saver).add(firstRecord);
        verify(saver).add(secondRecord);
    }
}
