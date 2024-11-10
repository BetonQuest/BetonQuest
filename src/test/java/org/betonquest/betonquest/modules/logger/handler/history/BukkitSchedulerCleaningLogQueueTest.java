package org.betonquest.betonquest.modules.logger.handler.history;

import org.betonquest.betonquest.util.scheduler.BukkitSchedulerMock;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link BukkitSchedulerCleaningLogQueue}.
 */
@ExtendWith(MockitoExtension.class)
class BukkitSchedulerCleaningLogQueueTest {
    /**
     * The current time used in the tests.
     */
    private final Instant now = Instant.now();

    /**
     * Fixed instant source returning {@link #now}.
     */
    private final InstantSource nowSource = InstantSource.fixed(now);

    /**
     * Duration that log entries are valid for.
     */
    private final Duration validFor = Duration.of(10, ChronoUnit.MINUTES);

    /**
     * Plugin to use.
     */
    @Mock
    private Plugin plugin;

    @Test
    void testSchedulerClearsOldRecords() {
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final BukkitSchedulerCleaningLogQueue logQueue = new BukkitSchedulerCleaningLogQueue(nowSource, validFor);
            logQueue.runCleanupTimerAsynchronously(scheduler, plugin, 20, 20);
            final LogRecord record = new LogRecord(Level.INFO, "old log record");
            record.setInstant(now.minus(validFor).minus(1, ChronoUnit.MINUTES));
            logQueue.push(record);
            scheduler.performTicks(20);
            scheduler.assertNoExceptions();
            assertFalse(logQueue.canPublish(), "the old record should have been discarded by the timed task");
        }
    }

    @Test
    void testSchedulerKeepsRecentRecords() {
        try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
            final BukkitSchedulerCleaningLogQueue logQueue = new BukkitSchedulerCleaningLogQueue(nowSource, validFor);
            logQueue.runCleanupTimerAsynchronously(scheduler, plugin, 20, 20);
            final LogRecord record = new LogRecord(Level.INFO, "recent log record");
            record.setInstant(now.minus(validFor));
            logQueue.push(record);
            scheduler.performTicks(20);
            scheduler.assertNoExceptions();
            assertTrue(logQueue.canPublish(), "the recent record should have been kept by the timed task");
        }
    }
}
