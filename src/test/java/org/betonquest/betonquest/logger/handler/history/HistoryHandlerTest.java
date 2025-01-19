package org.betonquest.betonquest.logger.handler.history;

import org.betonquest.betonquest.logger.handler.ResettableHandler;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.util.WriteOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * A test for the {@link HistoryHandler}.
 */
@ExtendWith(MockitoExtension.class)
class HistoryHandlerTest {
    /**
     * The logging enabled config value updater.
     */
    @Mock
    private WriteOperation<Boolean> loggingUpdater;

    @Test
    @SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.UnitTestContainsTooManyAsserts"})
    void testStartLoggingPrintsHistoryInCorrectOrder() throws IOException {
        final LogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final Handler validator = mock(Handler.class);
        final HistoryHandler historyHandler = new HistoryHandler(false, loggingUpdater, logQueue, new ResettableHandler(() -> validator));

        final Logger logger = BetonQuestLoggerService.getSilentLogger();
        logger.addHandler(historyHandler);
        logger.log(new LogRecord(Level.INFO, "record1"));

        verifyNoInteractions(validator);
        historyHandler.startLogging();
        verify(validator, times(1)).publish(argThat((record) -> "=====START OF HISTORY=====".equals(record.getMessage())));
        verify(validator, times(1)).publish(argThat((record) -> "record1".equals(record.getMessage())));
        verify(validator, times(1)).publish(argThat((record) -> "=====END OF HISTORY=====".equals(record.getMessage())));
        verifyNoMoreInteractions(validator);
        historyHandler.flush();
        historyHandler.close();
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testLoggingAfterStart() throws IOException {
        final LogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final Handler validator = mock(Handler.class);
        final HistoryHandler historyHandler = new HistoryHandler(false, loggingUpdater, logQueue, new ResettableHandler(() -> validator));

        final Logger logger = BetonQuestLoggerService.getSilentLogger();
        logger.addHandler(historyHandler);
        historyHandler.startLogging();
        logger.log(new LogRecord(Level.INFO, "record2"));

        verify(validator, times(1)).publish(argThat((record) -> "record2".equals(record.getMessage())));
        verifyNoMoreInteractions(validator);
        historyHandler.flush();
        historyHandler.close();
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testStartLoggingPublishesHistory(@Mock final ResettableHandler internalHandler) throws IOException {
        final LogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final HistoryHandler historyHandler = new HistoryHandler(false, loggingUpdater, logQueue, internalHandler);
        final LogRecord logRecord = new LogRecord(Level.INFO, "record");
        historyHandler.publish(logRecord);

        verifyNoInteractions(internalHandler);
        verifyNoInteractions(loggingUpdater);
        historyHandler.startLogging();
        verify(internalHandler).publish(argThat((record) -> "=====START OF HISTORY=====".equals(record.getMessage())));
        verify(internalHandler).publish(logRecord);
        verify(internalHandler).publish(argThat((record) -> "=====END OF HISTORY=====".equals(record.getMessage())));
        verify(loggingUpdater).write(true);
        verifyNoMoreInteractions(internalHandler);
        verifyNoMoreInteractions(loggingUpdater);
    }

    @Test
    void testNoHistoryMarkersWhenStartingWithEmptyHistory(@Mock final ResettableHandler internalHandler) throws IOException {
        final LogRecordQueue logQueue = new QueueBackedLogRecordQueue(new LinkedList<>());
        final HistoryHandler historyHandler = new HistoryHandler(false, loggingUpdater, logQueue, internalHandler);
        historyHandler.startLogging();
        verifyNoInteractions(internalHandler);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testCreatingHistoryLoggerDoesNotChangeConfig(final boolean initialDebuggingState,
                                                      @Mock final LogRecordQueue logQueue,
                                                      @Mock final ResettableHandler internalHandler) {
        new HistoryHandler(initialDebuggingState, loggingUpdater, logQueue, internalHandler);
        verifyNoInteractions(loggingUpdater);
    }

    @Test
    void testStartLoggingUpdatesConfig(@Mock final LogRecordQueue logQueue,
                                       @Mock final ResettableHandler internalHandler) throws IOException {
        final HistoryHandler historyHandler = new HistoryHandler(false, loggingUpdater, logQueue, internalHandler);
        historyHandler.startLogging();
        verify(loggingUpdater).write(true);
        verifyNoMoreInteractions(loggingUpdater);
    }

    @Test
    void testStopLoggingUpdatesConfig(@Mock final LogRecordQueue logQueue,
                                      @Mock final ResettableHandler internalHandler) throws IOException {
        final HistoryHandler historyHandler = new HistoryHandler(true, loggingUpdater, logQueue, internalHandler);
        historyHandler.stopLogging();
        verify(loggingUpdater).write(false);
        verifyNoMoreInteractions(loggingUpdater);
    }

    @Test
    void testDoubleStartingDoesNotUpdateConfig(@Mock final LogRecordQueue logQueue,
                                               @Mock final ResettableHandler internalHandler) throws IOException {
        final HistoryHandler historyHandler = new HistoryHandler(true, loggingUpdater, logQueue, internalHandler);
        historyHandler.startLogging();
        verifyNoInteractions(loggingUpdater);
    }

    @Test
    void testDoubleStoppingDoesNotUpdateConfig(@Mock final LogRecordQueue logQueue,
                                               @Mock final ResettableHandler internalHandler) throws IOException {
        final HistoryHandler historyHandler = new HistoryHandler(false, loggingUpdater, logQueue, internalHandler);
        historyHandler.stopLogging();
        verifyNoInteractions(loggingUpdater);
    }
}
