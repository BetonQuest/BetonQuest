package org.betonquest.betonquest.logger.handler.history;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * A queue that stores LogRecords to replay them at a later point in time. Different implementation may define their own
 * rules regarding whether to accept pushed records or when to discard them.
 */
public interface LogRecordQueue {

    /**
     * Push a {@link LogRecord} onto the queue.
     *
     * @param record record to push
     */
    void push(LogRecord record);

    /**
     * Check whether the queue is in a state where records can be published.
     *
     * @return true if the queue can publish records; false otherwise
     */
    boolean canPublish();

    /**
     * Publish the next record in the queue.
     *
     * @param publishingTarget the log Handler to publish the record to
     */
    void publishNext(Handler publishingTarget);

    /**
     * Publish all records in the queue.
     *
     * @param publishingTarget the log Handler to publish the record to
     */
    default void publishAll(final Handler publishingTarget) {
        while (canPublish()) {
            publishNext(publishingTarget);
        }
    }
}
