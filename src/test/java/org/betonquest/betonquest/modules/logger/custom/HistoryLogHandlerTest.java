package org.betonquest.betonquest.modules.logger.custom;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.junit.jupiter.api.Test;

import java.time.InstantSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * A test for the {@link HistoryLogHandler}.
 */
class HistoryLogHandlerTest {
    /**
     * A fixed time for this test to work with.
     */
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2022, 1, 1, 0, 0);
    /**
     * Is debug logging enabled during the test.
     */
    private boolean debugging;

    /**
     * Default constructor.
     */
    public HistoryLogHandlerTest() {
    }

    @Test
    void testLogHistory() {
        final InstantSource fixedTime = InstantSource.fixed(FIXED_TIME.toInstant(ZoneOffset.UTC));

        final BukkitSchedulerMock scheduler = new BukkitSchedulerMock();
        final Logger logger = LogValidator.getSilentLogger();
        final LogValidator validator = new LogValidator();
        final HistoryLogHandler history = new HistoryLogHandler(mock(BetonQuest.class), scheduler, validator, fixedTime, 20);

        logger.addHandler(history);
        history.setFilter(record -> debugging);

        createLogMessages(logger);
        scheduler.performTicks(20);
        scheduler.waitAsyncTasksFinished();
        assertLogMessages(validator, history);
    }

    private void createLogMessages(final Logger logger) {
        final BetonQuestLogRecord record1 = new BetonQuestLogRecord(null, null, Level.INFO, "Message 1");
        final BetonQuestLogRecord record2 = new BetonQuestLogRecord(null, null, Level.INFO, "Message 2");
        record1.setInstant(InstantSource.fixed(FIXED_TIME.minus(50, ChronoUnit.MINUTES).toInstant(ZoneOffset.UTC)).instant());
        record2.setInstant(InstantSource.fixed(FIXED_TIME.minus(10, ChronoUnit.MINUTES).toInstant(ZoneOffset.UTC)).instant());
        logger.log(record1);
        logger.log(record2);
    }

    private void assertLogMessages(final LogValidator validator, final HistoryLogHandler history) {
        validator.assertEmpty();
        debugging = true;
        history.push();
        validator.assertLogEntry(Level.INFO, HistoryLogHandler.START_OF_HISTORY);
        validator.assertLogEntry(Level.INFO, "Message 2");
        validator.assertLogEntry(Level.INFO, HistoryLogHandler.END_OF_HISTORY);
        validator.assertEmpty();
    }
}
