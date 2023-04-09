package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
public class AsyncSaver extends Thread implements Listener, Saver {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(AsyncSaver.class);

    /**
     * The connector that conntects to the database.
     */
    private final Connector con;

    /**
     * The queue of records to be saved to the database.
     */
    private final ConcurrentLinkedQueue<Record> queue;

    /**
     * Whether the saver is currently running or not.
     */
    private boolean running;

    /**
     * Creates new database saver thread.
     */
    public AsyncSaver() {
        super();
        this.con = new Connector();
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = true;
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    @SuppressFBWarnings("UW_UNCOND_WAIT")
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
                        LOG.error("There was a exception with SQL", e);
                    }
                }
            }
            if (!active) {
                con.refresh();
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
