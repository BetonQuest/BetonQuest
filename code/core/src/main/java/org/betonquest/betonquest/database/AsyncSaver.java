package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public class AsyncSaver implements Runnable, Saver {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The connector that connects to the database.
     */
    private final Connector con;

    /**
     * The queue of records to be saved to the database.
     */
    private final Queue<Record> queue;

    /**
     * Whether the saver is currently running or not.
     */
    private boolean running;

    /**
     * Creates a new database saver thread.
     *
     * @param log       the logger that will be used for logging
     * @param connector the connector for database access
     */
    public AsyncSaver(final BetonQuestLogger log, final Connector connector) {
        this.log = log;
        this.con = connector;
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = true;
    }

    @Override
    public void run() {
        log.debug("Thread started.");
        while (true) {
            synchronized (this) {
                while (queue.isEmpty()) {
                    if (!running) {
                        log.debug("Thread terminating.");
                        return;
                    }
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        log.warn("Got interrupted!");
                    }
                }
                consumeQueue();
            }
        }
    }

    private void consumeQueue() {
        while (!queue.isEmpty()) {
            final Record rec = queue.poll();
            if (rec != null) {
                log.debug("Processing queued record: '%s' with arguments '%s'".formatted(rec.type(), Arrays.toString(rec.args())));
                con.updateSQL(rec.type(), new Arguments(rec.args()));
            }
        }
    }

    @Override
    public void add(final Record rec) {
        synchronized (this) {
            log.debug("Queued record: '%s' with arguments '%s' (queued: %d)".formatted(rec.type(), Arrays.toString(rec.args()), queue.size() + 1));
            queue.add(rec);
            notifyAll();
        }
    }

    @Override
    public void end() {
        synchronized (this) {
            log.debug("Terminating with %d pending records in queue.".formatted(queue.size()));
            running = false;
            notifyAll();
        }
    }
}
