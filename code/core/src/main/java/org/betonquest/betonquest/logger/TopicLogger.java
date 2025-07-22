package org.betonquest.betonquest.logger;

import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A logger with an optional topic.
 */
public class TopicLogger extends Logger {
    /**
     * The topic of this logger.
     */
    private final String topic;

    /**
     * Creates a new {@link TopicLogger} that adds an optional topic.
     *
     * @param parentLogger A reference to the parent {@link Logger} which is used as parent for this logger.
     * @param clazz        The calling class.
     * @param topic        The topic to add or null.
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public TopicLogger(final Logger parentLogger, final Class<?> clazz, @Nullable final String topic) {
        super(clazz.getCanonicalName(), null);
        setParent(parentLogger);
        setLevel(Level.ALL);
        this.topic = topic == null ? "" : "(" + topic + ") ";
    }

    /**
     * Logs a LogRecord to the log with the topic.
     *
     * @param logRecord The record to log.
     */
    @Override
    public void log(final LogRecord logRecord) {
        logRecord.setMessage(topic + logRecord.getMessage());
        logRecord.setLoggerName(getParent().getName());
        super.log(logRecord);
    }
}
