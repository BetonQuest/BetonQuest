package org.betonquest.betonquest.logger.handler.history;

import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * LogRecordQueue implementation that adapts {@link Queue}.
 */
public class QueueBackedLogRecordQueue implements LogRecordQueue {

    /**
     * Internal Queue to store collected {@link LogRecord}s.
     */
    private final Queue<LogRecord> recordQueue;

    /**
     * Create the LogRecordQueue based on the given queue instance.
     *
     * @param recordQueue queue to use internally
     */
    public QueueBackedLogRecordQueue(final Queue<LogRecord> recordQueue) {
        this.recordQueue = recordQueue;
    }

    @Override
    public final void push(final LogRecord record) {
        getRecordQueue().add(record);
    }

    @Override
    public final boolean canPublish() {
        return !getRecordQueue().isEmpty();
    }

    @Override
    public final void publishNext(final Handler publishingTarget) {
        publishingTarget.publish(getRecordQueue().poll());
    }

    /**
     * Get the backing queue that is used internally to store {@link LogRecord}s.
     *
     * @return backing queue
     */
    public final Queue<LogRecord> getRecordQueue() {
        return recordQueue;
    }
}
