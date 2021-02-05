package org.betonquest.betonquest.utils.logger.custom;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
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
     * @param context A reference to the {@link Plugin} which is used to get the parent logger.
     * @param clazz   The calling class.
     * @param topic   The topic to add or null.
     */
    public TopicLogger(@NotNull final Plugin context, @NotNull final Class<?> clazz, @Nullable final String topic) {
        super(clazz.getCanonicalName(), null);
        setParent(context.getLogger());
        setLevel(Level.ALL);
        this.topic = topic == null ? "" : "(" + topic + ") ";
    }

    /**
     * Logs a LogRecord to the log with the topic.
     *
     * @param logRecord The record to log.
     */
    @Override
    public void log(@NotNull final LogRecord logRecord) {
        logRecord.setMessage(topic + logRecord.getMessage());
        logRecord.setLoggerName(getParent().getName());
        super.log(logRecord);
    }
}
