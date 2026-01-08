package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement"})
@SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
public class AsyncSaver extends Thread implements Saver {

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
     * The amount of time, until the AsyncSaver tries to reconnect if there was a connection loss.
     */
    private final long reconnectInterval;

    /**
     * Whether the saver is currently running or not.
     */
    private boolean running;

    /**
     * Creates new database saver thread.
     *
     * @param log               the logger that will be used for logging
     * @param reconnectInterval the interval for trying reconnecting to the database
     * @param connector         the connector for database access
     */
    public AsyncSaver(final BetonQuestLogger log, final long reconnectInterval, final Connector connector) {
        super();
        this.log = log;
        this.reconnectInterval = reconnectInterval;
        this.con = connector;
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = true;
    }

    @Override
    @SuppressFBWarnings("UW_UNCOND_WAIT")
    @SuppressWarnings("PMD.CognitiveComplexity")
    public void run() {
        boolean active = false;
        while (true) {
            while (queue.isEmpty()) {
                if (!running) {
                    return;
                }
                synchronized (this) {
                    try {
                        active = false;
                        wait();
                    } catch (final InterruptedException e) {
                        log.warn("AsyncSaver got interrupted!");
                    }
                }
            }
            while (!active) {
                try {
                    con.getDatabase().getConnection();
                    active = true;
                } catch (final IllegalStateException e) {
                    log.warn("Failed to re-establish connection with the database! Trying again in %s second(s)..."
                            .formatted(reconnectInterval / 1000), e);
                    try {
                        sleep(reconnectInterval);
                    } catch (final InterruptedException e1) {
                        log.warn("AsyncSaver got interrupted!", e1);
                        return;
                    }
                }
            }
            final Record rec = queue.poll();
            con.updateSQL(rec.type(), rec.args());
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
