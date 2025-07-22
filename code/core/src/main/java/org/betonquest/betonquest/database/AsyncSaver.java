package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement"})
@SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
public class AsyncSaver extends Thread implements Listener, Saver {

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
     * @param log    the logger that will be used for logging
     * @param config the plugin configuration file
     */
    public AsyncSaver(final BetonQuestLogger log, final ConfigAccessor config) {
        super();
        this.log = log;
        this.con = new Connector();
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = true;
        this.reconnectInterval = config.getLong("mysql.reconnect_interval");
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
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
            if (!active) {
                while (!con.refresh()) {
                    log.warn("Failed to re-establish connection with the database! Trying again in one second...");
                    try {
                        sleep(reconnectInterval);
                    } catch (final InterruptedException e) {
                        log.warn("AsyncSaver got interrupted!");
                    }
                }
                active = true;
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
