package org.betonquest.betonquest.modules.logger.custom.debug;

import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.custom.debug.config.DebugConfig;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.InstantSource;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This {@link Handler} can hold all {@link LogRecord}s from the configured log history time frame.
 * <br>
 * If the filter returns true, a LogRecord will be passed to the target {@link Handler},
 * otherwise it will be written to the history.
 * The history can then be pushed to the target handler at any time.
 * It is automatically pushed if the filter returns true for any subsequent LogRecord.
 */
public class HistoryHandler extends Handler {
    /**
     * The message printed before the history is printed.
     */
    public static final String START_OF_HISTORY = "=====START OF HISTORY=====";
    /**
     * The message printed after the history is printed.
     */
    public static final String END_OF_HISTORY = "=====END OF HISTORY=====";

    /**
     * The {@link LogRecord} history.
     */
    private final Queue<LogRecord> records;
    /**
     * Lock for publishing {@link LogRecord}s to maintain chronological order while publishing the accumulated history.
     */
    private final Lock publishLock;
    /**
     * The target Handler to log the history to.
     */
    private final ResettableHandler target;

    /**
     * The {@link DebugConfig} for this {@link Handler}.
     */
    private final DebugConfig debugConfig;

    /**
     * Creates a new {@link HistoryHandler}.
     *
     * @param debugConfig   the config for the settings
     * @param plugin        the plugin, that should own the task for cleanups
     * @param scheduler     the scheduler for the cleanup task
     * @param target        the Handler to log the history to
     * @param instantSource the {@link InstantSource} to get the {@link java.time.Instant} from
     */
    public HistoryHandler(final DebugConfig debugConfig, final Plugin plugin, final BukkitScheduler scheduler, final ResettableHandler target,
                          final InstantSource instantSource) {
        super();
        this.debugConfig = debugConfig;
        debugConfig.addOnStartHandler(this, new PushHistoryRunnable());
        debugConfig.addOnStopHandler(this, new ResetTargetRunnable());
        if (debugConfig.getExpireAfterMinutes() == 0) {
            this.records = null;
        } else {
            this.records = new ConcurrentLinkedQueue<>();
            scheduler.runTaskTimerAsynchronously(plugin, () -> {
                while (isExpired(records.peek(), instantSource, debugConfig.getExpireAfterMinutes() * 60 * 1000L)) {
                    records.remove();
                }
            }, 20, 20);
        }
        this.target = target;
        this.publishLock = new ReentrantLock(true);
    }

    private boolean isExpired(final LogRecord record, final InstantSource instantSource, final long afterMillis) {
        return record != null && record.getInstant().isBefore(instantSource.instant().minusMillis(afterMillis));
    }

    /**
     * Logs a LogRecord to the history or the target handler.
     *
     * @param record The {@link LogRecord} to log
     */
    @Override
    public void publish(final LogRecord record) {
        if (!(record instanceof BetonQuestLogRecord) || !isLoggable(record)) {
            return;
        }
        if (debugConfig.isDebugging()) {
            publishLock.lock();
            try {
                target.publish(record);
            } finally {
                publishLock.unlock();
            }
        } else if (records != null) {
            records.add(record);
        }
    }

    /**
     * Publishes any available history to the target handler.
     */
    private void push() {
        if (records != null && !records.isEmpty()) {
            target.publish(new LogRecord(Level.INFO, START_OF_HISTORY));
            while (records.peek() != null) {
                target.publish(records.poll());
            }
            target.publish(new LogRecord(Level.INFO, END_OF_HISTORY));
        }
    }

    /**
     * Flushes the target handler.
     */
    @Override
    public void flush() {
        target.flush();
    }

    /**
     * Closes the target handler.
     */
    @Override
    public void close() {
        debugConfig.removeOnStartHandler(this);
        target.close();
    }

    /**
     * Get the {@link DebugConfig} related to this {@link HistoryHandler}
     *
     * @return a {@link DebugConfig} instance
     */
    public DebugConfig getDebugConfig() {
        return debugConfig;
    }

    /**
     * Implementation of {@link DebugConfig.PrePostRunnable} for publishing the history.
     */
    private class PushHistoryRunnable implements DebugConfig.PrePostRunnable {
        /**
         * Default constructor.
         */
        public PushHistoryRunnable() {
            // Empty
        }

        @Override
        public void preRun() {
            publishLock.lock();
        }

        @Override
        public void postRun() {
            publishLock.unlock();
        }

        @Override
        public void run() {
            push();
        }
    }

    /**
     * Implementation for {@link DebugConfig.PrePostRunnable} for resetting the target handler.
     */
    private class ResetTargetRunnable implements DebugConfig.PrePostRunnable {
        /**
         * Default constructor.
         */
        public ResetTargetRunnable() {
            // Empty
        }

        @Override
        public void preRun() {
            publishLock.lock();
        }

        @Override
        public void postRun() {
            publishLock.unlock();
        }

        @Override
        public void run() {
            target.reset();
        }
    }
}
