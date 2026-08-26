package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement"})
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
     * The amount of time until the AsyncSaver tries to reconnect if there was a connection loss.
     */
    private final long reconnectInterval;

    /**
     * Whether the saver is currently running or not.
     */
    private boolean running;

    /**
     * Creates a new database saver thread.
     *
     * @param log               the logger that will be used for logging
     * @param reconnectInterval the interval for trying reconnecting to the database
     * @param connector         the connector for database access
     */
    public AsyncSaver(final BetonQuestLogger log, final long reconnectInterval, final Connector connector) {
        this.log = log;
        this.reconnectInterval = reconnectInterval;
        this.con = connector;
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = true;
    }

    @Override
    @SuppressFBWarnings("UW_UNCOND_WAIT")
    public void run() {
        log.debug("Thread started.");
        boolean active = false;
        while (true) {
            while (queue.isEmpty()) {
                if (!running) {
                    log.debug("Thread terminating.");
                    return;
                }
                synchronized (this) {
                    try {
                        active = false;
                        wait();
                    } catch (final InterruptedException e) {
                        log.warn("Got interrupted!");
                    }
                }
            }
            if (!(active || ensureActive())) {
                return;
            }
            active = true;
            final Record rec = queue.poll();
            if (rec != null) {
                log.debug("Processing queued record: '%s' with arguments '%s'".formatted(rec.type(), Arrays.toString(rec.args())));
                con.updateSQL(rec.type(), new Arguments(rec.args()));
            }
        }
    }

    private boolean ensureActive() {
        try (Connection connection = con.getDatabase().getConnection()) {
            log.debug("Validating database connection...");
            if (connection.isValid(5000)) {
                log.debug("Database connection validated.");
                return true;
            }
        } catch (final IllegalStateException | SQLException illegalStateException) {
            log.warn("Failed to re-establish connection with the database! Trying again in %s second(s)..."
                    .formatted(reconnectInterval / 1000), illegalStateException);
            try {
                Thread.sleep(reconnectInterval);
            } catch (final InterruptedException interruptedException) {
                log.warn("Got interrupted!", interruptedException);
                return false;
            }
        }
        return ensureActive();
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
