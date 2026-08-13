package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
@SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
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
    @SuppressFBWarnings("UW_UNCOND_WAIT")
    public void run() {
        while (true) {
            while (queue.isEmpty()) {
                if (!running) {
                    return;
                }
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        log.warn("AsyncSaver got interrupted!");
                    }
                }
            }
            final Record rec = queue.poll();
            if (rec != null) {
                con.updateSQL(rec.type(), new Arguments(rec.args()));
            }
        }
    }

    @Override
    public void add(final Record rec) {
        synchronized (this) {
            queue.add(rec);
            notifyAll();
        }
    }

    @Override
    public void end() {
        synchronized (this) {
            running = false;
            notifyAll();
        }
    }
}
