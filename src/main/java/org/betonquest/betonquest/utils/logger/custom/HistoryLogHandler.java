package org.betonquest.betonquest.utils.logger.custom;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.betonquest.betonquest.utils.logger.QuestPackageLogRecord;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a {@link Handler} that can hold the last 10 minutes of {@link LogRecord}s.
 * If the filter returns true, the LogRecords will be passed to the target logger
 * otherwise they will be written to the history.
 * The history can then be pushed to the target handler at any time.
 * It is automatically pushed if the next LogRecord is logged and the filter returns true.
 */
public class HistoryLogHandler extends Handler {
    /**
     * The history of the last 10 minutes of LogRecords.
     */
    private final Cache<Integer, LogRecord> records = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
    /**
     * A lock to prevent read and write at the same time.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    /**
     * The target Handler to log the history to.
     */
    private final Handler target;

    /**
     * Creates a new {@link HistoryLogHandler}.
     *
     * @param target The Handler to log the history to
     */
    public HistoryLogHandler(final Handler target) {
        super();
        this.target = target;
    }

    /**
     * Logs a LogRecord to the history or the target handler.
     *
     * @param record The LogRecord to log
     */
    @Override
    public void publish(final LogRecord record) {
        if (!(record instanceof QuestPackageLogRecord)) {
            return;
        }
        lock.writeLock().lock();
        try {
            if (isLoggable(record)) {
                push();
                target.publish(record);
            } else {
                records.put((int) records.size(), record);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Publishes the history to the target handler if history is available.
     */
    public void push() {
        lock.readLock().lock();
        try {
            if (records.size() > 0) {
                target.publish(new LogRecord(Level.INFO, "=====START OF HISTORY====="));
                for (int i = 0; i < records.size(); i++) {
                    target.publish(records.getIfPresent(i));
                }
                target.publish(new LogRecord(Level.INFO, "=====END OF HISTORY====="));
                records.invalidateAll();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Flushes the target handler.
     */
    @Override
    public void flush() {
        target.flush();
    }

    /**
     * Closes the target Handler.
     */
    @Override
    public void close() {
        target.close();
    }
}
