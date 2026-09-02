package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class AsyncSaver implements Saver {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The connector that connects to the database.
     */
    private final Connector con;

    /**
     * The executor service that will run the saver.
     */
    private final ExecutorService executor;

    /**
     * Creates a new database saver thread.
     *
     * @param log       the logger that will be used for logging
     * @param connector the connector for database access
     */
    public AsyncSaver(final BetonQuestLogger log, final Connector connector) {
        this.log = log;
        this.con = connector;
        this.executor = Executors.newSingleThreadExecutor();
    }

    private void process(final Record rec) {
        log.debug("Processing record: '%s' with arguments '%s'".formatted(rec.type(), Arrays.toString(rec.args())));
        con.updateSQL(rec.type(), new Arguments(rec.args()));
        log.debug("Processing record done: '%s' with arguments '%s'".formatted(rec.type(), Arrays.toString(rec.args())));
    }

    @Override
    public void add(final Record rec) {
        executor.execute(() -> process(rec));
    }

    @Override
    public void end() {
        log.debug("Terminating executor service.");
        try {
            executor.shutdown();
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.debug("Executor service terminated.");
            } else {
                log.error("Executor service did not terminate correctly!");
            }
        } catch (final InterruptedException e) {
            log.error("Interrupted while waiting for executor service to terminate.", e);
        }
    }
}
