package org.betonquest.betonquest.modules.logger.custom.debug;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.custom.debug.config.DebugConfig;
import org.betonquest.betonquest.modules.logger.custom.debug.config.SimpleDebugConfig;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.InstantSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * A test for the {@link HistoryHandler}.
 */
class HistoryHandlerTest {
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
    public HistoryHandlerTest() {
        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("BetonQuest");
    }

    @Test
    void testLogHistory() throws IOException {
        final InstantSource fixedTime = InstantSource.fixed(FIXED_TIME.toInstant(ZoneOffset.UTC));

        final LogValidator validator;
        final HistoryHandler historyHandler;
        final Logger logger = LogValidator.getSilentLogger();
        validator = new LogValidator();
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final DebugConfig debugConfig = new SimpleDebugConfig(false);
            historyHandler = new HistoryHandler(debugConfig, mock(BetonQuest.class), scheduler, validator, fixedTime);

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

    private void assertLogMessages(final LogValidator validator, final HistoryHandler historyHandler) throws IOException {
        validator.assertEmpty();
        historyHandler.getDebugConfig().startDebug();
        validator.assertLogEntry(Level.INFO, HistoryHandler.START_OF_HISTORY);
        validator.assertLogEntry(Level.INFO, "Message 2");
        validator.assertLogEntry(Level.INFO, HistoryHandler.END_OF_HISTORY);
        validator.assertEmpty();
    }
}
