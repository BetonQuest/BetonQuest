package org.betonquest.betonquest.modules.logger.custom;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.bukkit.plugin.Plugin;
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
     * The mocked plugin instance.
     */
    private final Plugin plugin;

    /**
     * Default constructor.
     */
    public HistoryLogHandlerTest() {
        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("BetonQuest");
    }

    @Test
    void testLogHistory() {
        final InstantSource fixedTime = InstantSource.fixed(FIXED_TIME.toInstant(ZoneOffset.UTC));

        final LogValidator validator;
        final HistoryLogHandler historyHandler;
        final Logger logger = LogValidator.getSilentLogger();
        validator = new LogValidator();
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            historyHandler = new HistoryLogHandler(mock(BetonQuest.class), scheduler, validator, fixedTime, 20);

            logger.addHandler(historyHandler);

            createLogMessages(logger);
            scheduler.performTicks(20);
            scheduler.assertNoExceptions();
        }
        assertLogMessages(validator, historyHandler);
    }

    private void createLogMessages(final Logger logger) {
        final BetonQuestLogRecord record1 = new BetonQuestLogRecord(plugin, null, Level.INFO, "Message 1");
        final BetonQuestLogRecord record2 = new BetonQuestLogRecord(plugin, null, Level.INFO, "Message 2");
        record1.setInstant(InstantSource.fixed(FIXED_TIME.minus(50, ChronoUnit.MINUTES).toInstant(ZoneOffset.UTC)).instant());
        record2.setInstant(InstantSource.fixed(FIXED_TIME.minus(10, ChronoUnit.MINUTES).toInstant(ZoneOffset.UTC)).instant());
        logger.log(record1);
        logger.log(record2);
    }

    private void assertLogMessages(final LogValidator validator, final HistoryLogHandler historyHandler) {
        validator.assertEmpty();
        historyHandler.startDebug();
        validator.assertLogEntry(Level.INFO, HistoryLogHandler.START_OF_HISTORY);
        validator.assertLogEntry(Level.INFO, "Message 2");
        validator.assertLogEntry(Level.INFO, HistoryLogHandler.END_OF_HISTORY);
        validator.assertEmpty();
    }
}
