package org.betonquest.betonquest.modules.logger.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class can be attached to any {@link java.util.logging.Logger} as handler.
 * Then it is possible to check for {@link LogRecord}s to assert that the right things are logged.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class LogValidator extends Handler {
    /**
     * The queue of all left {@link LogRecord}s.
     */
    private final Queue<LogRecord> records;

    /**
     * Create a new {@link LogValidator}.
     */
    public LogValidator() {
        super();
        records = new ConcurrentLinkedQueue<>();
    }

    /**
     * Creates an anonymous and silent logger. This is an optimal way to obtain a logger for testing.
     *
     * @return a silent logger
     */
    public static Logger getSilentLogger() {
        final Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        return logger;
    }

    /**
     * Setup and return a {@link LogValidator} for the given logger.
     *
     * @param logger The related logger.
     * @return The LogValidator.
     */
    public static LogValidator getForLogger(final Logger logger) {
        final LogValidator logValidator = new LogValidator();
        logger.addHandler(logValidator);
        return logValidator;
    }

    @Override
    public void publish(final LogRecord record) {
        records.add(record);
    }

    @Override
    public void flush() {
        records.clear();
    }

    @Override
    public void close() {
        // Empty
    }

    /**
     * Assert that the {@link LogValidator} does not have any LogRecord left to check.
     */
    public void assertEmpty() {
        assertTrue(records.isEmpty(), "The records list should be empty!");
    }

    /**
     * Assert that the level and the message and the throwable class of the LogRecord are equal.
     *
     * @param level            The expected level.
     * @param message          The expected message.
     * @param throwable        The expected throwable class.
     * @param throwableMessage The expected throwable message.
     */
    public void assertLogEntry(final Level level, final String message, final Class<? extends Throwable> throwable, final String throwableMessage) {
        assertEntry(popLogRecord(), level, message, throwable, throwableMessage);
    }

    /**
     * Assert that the level and the message and the throwable class of the LogRecord are equal.
     *
     * @param level     The expected level.
     * @param message   The expected message.
     * @param throwable The expected throwable class.
     */
    public void assertLogEntry(final Level level, final String message, final Class<? extends Throwable> throwable) {
        assertEntry(popLogRecord(), level, message, throwable);
    }

    /**
     * Assert that the level and the message of the LogRecord are equal.
     *
     * @param level   The expected level.
     * @param message The expected message.
     */
    public void assertLogEntry(final Level level, final String message) {
        assertEntry(popLogRecord(), level, message);
    }

    private LogRecord popLogRecord() {
        assertFalse(records.isEmpty(), "There is no LogRecord left to query for assertion!");
        return records.remove();
    }

    private void assertEntry(final LogRecord record, final Level level, final String message, final Class<? extends Throwable> throwable, final String throwableMessage) {
        assertEntry(record, level, message, throwable);
        assertEquals(throwableMessage, record.getThrown().getMessage(), "Expected log throwable message does not equal!");
    }

    private void assertEntry(final LogRecord record, final Level level, final String message, final Class<? extends Throwable> throwable) {
        assertEntry(record, level, message);
        assertNotNull(record.getThrown(), "Expected log throwable is null!");
        assertEquals(throwable, record.getThrown().getClass(), "Expected log throwable does not equal!");
    }

    private void assertEntry(final LogRecord record, final Level level, final String message) {
        assertNotNull(record, "The record is unexpected null!");
        assertEquals(level, record.getLevel(), "Expected log level does not equal!");
        assertEquals(message, record.getMessage(), "Expected log message does not equal!");
    }
}
