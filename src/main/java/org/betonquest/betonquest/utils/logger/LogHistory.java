package org.betonquest.betonquest.utils.logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This class can hold a history of 10 minutes of {@link LogRecord}'s
 * and write them in to a {@link FileHandler} at any time.
 */
public class LogHistory {
    /**
     * The index of the current history position
     */
    private static int index;
    /**
     * A cache of the last 10 minutes of {@link LogRecord}'s
     */
    private final Cache<Integer, LogRecord> records = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * Create a new {@link LogHistory}
     */
    public LogHistory() {
    }

    /**
     * Add {@link LogRecord}'s to the history.
     *
     * @param record The record to log to the history.
     */
    public void logToHistory(final LogRecord record) {
        synchronized (LogHistory.class) {
            records.put(index++, record);
        }
    }

    /**
     * Writes the history to the {@link FileHandler} and clears the history.
     *
     * @param fileHandler The {@link FileHandler} to write the history to.
     */
    public void writeHistory(final FileHandler fileHandler) {
        synchronized (LogHistory.class) {
            final boolean hasHistory = records.size() > 0;
            if (hasHistory) {
                fileHandler.publish(new LogRecord(Level.INFO, "=====START OF HISTORY====="));
            }
            for (final LogRecord record : records.asMap().values()) {
                fileHandler.publish(record);
            }
            if (hasHistory) {
                fileHandler.publish(new LogRecord(Level.INFO, "=====END OF HISTORY====="));
                records.cleanUp();
                index = 0;
            }
        }
    }
}
