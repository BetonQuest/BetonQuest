package org.betonquest.betonquest.logger.handler.history;

import org.betonquest.betonquest.logger.handler.ResettableHandler;
import org.betonquest.betonquest.util.WriteOperation;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This {@link Handler} can hold all {@link LogRecord}s from the configured log history time frame.
 * <br>
 * If the filter returns true, a LogRecord will be passed to the target {@link Handler},
 * otherwise it will be written to the history.
 * The history can then be pushed to the target handler at any time.
 * It is automatically pushed if the filter returns true for any subsequent LogRecord.
 */
public class HistoryHandler extends Handler implements LogPublishingController {
    /**
     * The message printed before the history is printed.
     */
    private static final String START_OF_HISTORY = "=====START OF HISTORY=====";

    /**
     * The message printed after the history is printed.
     */
    private static final String END_OF_HISTORY = "=====END OF HISTORY=====";

    /**
     * The {@link LogRecord} history.
     */
    private final LogRecordQueue recordQueue;

    /**
     * Lock for publishing {@link LogRecord}s to maintain chronological order while publishing the accumulated history.
     */
    private final Lock publishLock;

    /**
     * The target Handler to log the history to.
     */
    private final ResettableHandler target;

    /**
     * The configuration updater for the debugging mode.
     */
    private final WriteOperation<Boolean> loggingStateUpdater;

    /**
     * Whether debugging is enabled.
     */
    private boolean logging;

    /**
     * Creates a new {@link HistoryHandler}.
     *
     * @param logging the initial logging state
     * @param loggingStateUpdater the config for the settings
     * @param recordQueue the queue for storing records while not logging
     * @param target the Handler to log the history to
     */
    public HistoryHandler(final boolean logging, final WriteOperation<Boolean> loggingStateUpdater,
                          final LogRecordQueue recordQueue, final ResettableHandler target) {
        super();
        this.logging = logging;
        this.loggingStateUpdater = loggingStateUpdater;
        this.recordQueue = recordQueue;
        this.target = target;
        this.publishLock = new ReentrantLock(true);
    }

    /**
     * Logs a LogRecord to the history or the target handler.
     *
     * @param record The {@link LogRecord} to log
     */
    @Override
    public void publish(final LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        if (isLogging()) {
            publishLock.lock();
            try {
                target.publish(record);
            } finally {
                publishLock.unlock();
            }
        } else {
            recordQueue.push(record);
        }
    }

    @Override
    public void flush() {
        target.flush();
    }

    @Override
    public void close() {
        target.close();
    }

    /**
     * Get the {@link LogPublishingController} related to this {@link HistoryHandler}.
     *
     * @return a {@link LogPublishingController} instance
     */
    @Override
    public boolean isLogging() {
        return logging;
    }

    private void setLogging(final boolean logging) throws IOException {
        loggingStateUpdater.write(logging);
        this.logging = logging;
    }

    @Override
    public void startLogging() throws IOException {
        publishLock.lock();
        try {
            if (!isLogging()) {
                setLogging(true);
                push();
            }
        } finally {
            publishLock.unlock();
        }
    }

    @Override
    public void stopLogging() throws IOException {
        publishLock.lock();
        try {
            if (isLogging()) {
                setLogging(false);
                target.reset();
            }
        } finally {
            publishLock.unlock();
        }
    }

    /**
     * Publishes any available history to the target handler.
     */
    private void push() {
        if (recordQueue.canPublish()) {
            target.publish(new LogRecord(Level.INFO, START_OF_HISTORY));
            recordQueue.publishAll(target);
            target.publish(new LogRecord(Level.INFO, END_OF_HISTORY));
        }
    }
}
