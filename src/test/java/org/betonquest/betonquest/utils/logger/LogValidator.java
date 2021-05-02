package org.betonquest.betonquest.utils.logger;

import org.junit.jupiter.api.Assertions;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class an be attached to any {@link java.util.logging.Logger} as handler.
 * Then it is possible to check for {@link LogRecord}s to assert that the right things are logged.
 */
public class LogValidator extends Handler {
    /**
     * The que of all left {@link LogRecord}s.
     */
    private final Deque<LogRecord> records;

    /**
     * Create a new {@link LogValidator}.
     */
    public LogValidator() {
        super();
        records = new LinkedList<>();
    }

    @Override
    public void publish(final LogRecord record) {
        records.addLast(record);
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
        Assertions.assertTrue(records.isEmpty(), "The records list should be empty!");
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
        final LogRecord record = records.pop();
        assertNotNull(record, "The record is unexpected null!");
        assertEntry(record, level, message);
        assertEntry(record, throwable);
        assertEntry(record, throwableMessage);
    }

    /**
     * Assert that the level and the message and the throwable class of the LogRecord are equal.
     *
     * @param level     The expected level.
     * @param message   The expected message.
     * @param throwable The expected throwable class.
     */
    public void assertLogEntry(final Level level, final String message, final Class<? extends Throwable> throwable) {
        final LogRecord record = records.pop();
        assertNotNull(record, "The record is unexpected null!");
        assertEntry(record, level, message);
        assertEntry(record, throwable);
    }

    /**
     * Assert that the level and the message of the LogRecord are equal.
     *
     * @param level   The expected level.
     * @param message The expected message.
     */
    public void assertLogEntry(final Level level, final String message) {
        final LogRecord record = records.pop();
        assertNotNull(record, "The record is unexpected null!");
        assertEntry(record, level, message);
    }

    private void assertEntry(final LogRecord record, final String throwableMessage) {
        assertEquals(throwableMessage, record.getThrown().getMessage(), "Expected log throwable message does not equal!");
    }

    private void assertEntry(final LogRecord record, final Class<? extends Throwable> throwable) {
        assertNotNull(record.getThrown(), "Expected log throwable is null!");
        assertEquals(throwable, record.getThrown().getClass(), "Expected log throwable does not equal!");
    }

    private void assertEntry(final LogRecord record, final Level level, final String message) {
        assertEquals(level, record.getLevel(), "Expected log level does not equal!");
        assertEquals(message, record.getMessage(), "Expected log message does not equal!");
    }
}
