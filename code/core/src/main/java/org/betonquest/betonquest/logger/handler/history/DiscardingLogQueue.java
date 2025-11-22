package org.betonquest.betonquest.logger.handler.history;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * LogRecord Queue that discards any pushed element. It is a Null Pattern implementation.
 */
public class DiscardingLogQueue implements LogRecordQueue {
    /**
     * Create a discarding LogRecord queue.
     */
    public DiscardingLogQueue() {
    }

    @Override
    public void push(final LogRecord record) {
        // null object pattern
    }

    @Override
    public boolean canPublish() {
        return false;
    }

    @Override
    public void publishNext(final Handler publishingTarget) {
        throw new UnsupportedOperationException("A discarding log queue can't publish log records as it doesn't keep any.");
    }
}
