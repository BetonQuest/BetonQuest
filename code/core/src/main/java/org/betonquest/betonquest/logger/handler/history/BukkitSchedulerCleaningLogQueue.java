package org.betonquest.betonquest.logger.handler.history;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.InstantSource;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.LogRecord;

/**
 * LogRecord Queue that cleans old records by using a {@link org.bukkit.scheduler.BukkitScheduler} to drop expired
 * records regularly.
 */
public class BukkitSchedulerCleaningLogQueue extends QueueBackedLogRecordQueue implements LogRecordQueue {

    /**
     * Time that new log records are valid for.
     */
    private final Duration validFor;

    /**
     * Instant source to get the current time when comparing {@link LogRecord}s.
     */
    private final InstantSource instantSource;

    /**
     * Create a log record queue cleaned by a bukkit scheduler. The scheduler will not be started until
     * {@link #runCleanupTimerAsynchronously(BukkitScheduler, Plugin, long, long)} is called.
     *
     * @param instantSource instant source
     * @param validFor      duration that log records should be valid for
     */
    public BukkitSchedulerCleaningLogQueue(final InstantSource instantSource, final Duration validFor) {
        super(new ConcurrentLinkedQueue<>());
        this.validFor = validFor;
        this.instantSource = instantSource;
    }

    /**
     * Start a new task that regularly removes old entries from the log queue.
     *
     * @param scheduler scheduler to run the task in
     * @param plugin    plugin to schedule for
     * @param delay     the ticks to wait before running the task for the first time
     * @param period    the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     */
    public BukkitTask runCleanupTimerAsynchronously(final BukkitScheduler scheduler, final Plugin plugin, final long delay, final long period) {
        return scheduler.runTaskTimerAsynchronously(plugin, () -> {
            while (isExpired(getRecordQueue().peek())) {
                getRecordQueue().remove();
            }
        }, delay, period);
    }

    private boolean isExpired(@Nullable final LogRecord record) {
        return record != null && record.getInstant().isBefore(instantSource.instant().minus(validFor));
    }
}
