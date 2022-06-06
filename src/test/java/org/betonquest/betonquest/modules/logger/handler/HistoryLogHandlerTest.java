package org.betonquest.betonquest.modules.logger.handler;

import org.betonquest.betonquest.modules.logger.queue.LogRecordQueue;
import org.betonquest.betonquest.modules.logger.queue.QueueBackedLogRecordQueue;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * A test for the {@link HistoryLogHandler}.
 */
@ExtendWith(MockitoExtension.class)
class HistoryLogHandlerTest {
    /**
     * The debug config.
     */
    private final DebugConfig debugConfig = new MemoryDebugConfig(false);

    /**
     * Default constructor.
     */
    public HistoryLogHandlerTest() {
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testStartLoggingPrintsHistoryInCorrectOrder(@Mock final DebugConfig debugConfig) throws IOException {
        final LogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final LogValidator validator = new LogValidator();
        final HistoryLogHandler historyHandler = new HistoryLogHandler(debugConfig, logQueue, new ResettableLogHandler(() -> validator));

        final Logger logger = LogValidator.getSilentLogger();
        logger.addHandler(historyHandler);
        logger.log(new LogRecord(Level.INFO, "record"));

        validator.assertEmpty();
        historyHandler.startLogging();
        validator.assertLogEntry(Level.INFO, "=====START OF HISTORY=====");
        validator.assertLogEntry(Level.INFO, "record");
        validator.assertLogEntry(Level.INFO, "=====END OF HISTORY=====");
        validator.assertEmpty();
    }

    @Test
    void testStartLoggingPublishesHistory(@Mock final ResettableLogHandler internalHandler) throws IOException {
        final LogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final HistoryLogHandler historyHandler = new HistoryLogHandler(debugConfig, logQueue, internalHandler);
        final LogRecord logRecord = new LogRecord(Level.INFO, "record");
        historyHandler.publish(logRecord);

        verifyNoInteractions(internalHandler);
        historyHandler.startLogging();
        verify(internalHandler).publish(argThat((record) -> "=====START OF HISTORY=====".equals(record.getMessage())));
        verify(internalHandler).publish(logRecord);
        verify(internalHandler).publish(argThat((record) -> "=====END OF HISTORY=====".equals(record.getMessage())));
        verifyNoMoreInteractions(internalHandler);
    }

    @Test
    void testNoHistoryMarkersWhenStartingWithEmptyHistory(@Mock final ResettableLogHandler internalHandler) throws IOException {
        final LogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final HistoryLogHandler historyHandler = new HistoryLogHandler(debugConfig, logQueue, internalHandler);
        historyHandler.startLogging();
        verifyNoInteractions(internalHandler);
    }

}
