package org.betonquest.betonquest.logger.handler.history;

import org.junit.jupiter.api.Test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link DiscardingLogQueue}.
 */
class DiscardingLogQueueTest {

    @Test
    void testCanPush() {
        final DiscardingLogQueue logQueue = new DiscardingLogQueue();
        assertFalse(logQueue.canPublish(), "canPublish should be false");
        logQueue.push(new LogRecord(Level.INFO, ""));
        assertFalse(logQueue.canPublish(), "canPublish should still be false");
    }

    @Test
    void testPublishNext() {
        final DiscardingLogQueue logQueue = new DiscardingLogQueue();
        assertThrows(UnsupportedOperationException.class, () -> logQueue.publishNext(mock(Handler.class)),
                "publishNext should throw UnsupportedOperationException exception");
        logQueue.push(new LogRecord(Level.INFO, ""));
        assertThrows(UnsupportedOperationException.class, () -> logQueue.publishNext(mock(Handler.class)),
                "publishNext should still throw UnsupportedOperationException exception");
    }
}
