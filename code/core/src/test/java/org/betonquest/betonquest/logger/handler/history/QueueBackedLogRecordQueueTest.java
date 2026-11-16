package org.betonquest.betonquest.logger.handler.history;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link QueueBackedLogRecordQueue}.
 */
@ExtendWith(MockitoExtension.class)
class QueueBackedLogRecordQueueTest {

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void log_entries_are_in_order(@Mock final Handler handler) {
        final QueueBackedLogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final LogRecord firstRecord = new LogRecord(Level.INFO, "record 1");
        final LogRecord secondRecord = new LogRecord(Level.INFO, "record 2");
        logQueue.push(firstRecord);
        logQueue.push(secondRecord);
        logQueue.publishNext(handler);
        verify(handler).publish(firstRecord);
        verify(handler, never()).publish(secondRecord);
        logQueue.publishNext(handler);
        verify(handler).publish(firstRecord);
        verify(handler).publish(secondRecord);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void publish_all_empties_the_queue(@Mock final Handler handler) {
        final QueueBackedLogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final LogRecord firstRecord = new LogRecord(Level.INFO, "record 1");
        final LogRecord secondRecord = new LogRecord(Level.INFO, "record 2");
        logQueue.push(firstRecord);
        logQueue.push(secondRecord);
        logQueue.publishAll(handler);
        assertFalse(logQueue.canPublish(), "queue should be empty after publishing all records");
        verify(handler).publish(firstRecord);
        verify(handler).publish(secondRecord);
    }

    @Test
    void can_publish_with_at_least_one_record() {
        final QueueBackedLogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        assertFalse(logQueue.canPublish(), "empty queue should not allow publishing");
        logQueue.push(new LogRecord(Level.INFO, "record"));
        assertTrue(logQueue.canPublish(), "filled queue should allow publishing");
    }
}
