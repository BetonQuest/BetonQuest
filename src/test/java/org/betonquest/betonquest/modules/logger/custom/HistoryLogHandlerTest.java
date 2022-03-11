package org.betonquest.betonquest.modules.logger.custom;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * A test for the {@link HistoryLogHandler}.
 */
class HistoryLogHandlerTest {
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
    void testLogHistory() throws InterruptedException {
        final BukkitSchedulerMock scheduler = new BukkitSchedulerMock();
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);

            final Logger logger = LogValidator.getSilentLogger();
            final LogValidator validator = new LogValidator();
            final HistoryLogHandler history = new HistoryLogHandler(mock(BetonQuest.class), validator, 0.0025);

            logger.addHandler(history);
            history.setFilter(record -> debugging);

            createLogMessages(logger, scheduler);
            assertLogMessages(validator, history);
        }
    }

    private void createLogMessages(final Logger logger, final BukkitSchedulerMock scheduler) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            logger.log(new BetonQuestLogRecord(null, null, Level.INFO, "Message " + i));
            Thread.sleep(100);
            scheduler.performTicks(10);
        }
    }

    private void assertLogMessages(final LogValidator validator, final HistoryLogHandler history) {
        validator.assertEmpty();
        debugging = true;
        history.push();
        validator.assertLogEntry(Level.INFO, HistoryLogHandler.START_OF_HISTORY);
        for (int i = 1; i < 2; i++) {
            validator.assertLogEntry(Level.INFO, "Message " + i);
        }
        validator.assertLogEntry(Level.INFO, HistoryLogHandler.END_OF_HISTORY);
        validator.assertEmpty();
    }
}
