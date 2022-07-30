package org.betonquest.betonquest.modules.schedule.impl;

import lombok.CustomLog;
import org.betonquest.betonquest.api.annotation.VisibleForTesting;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.modules.schedule.impl.realtime.daily.RealtimeDailyScheduler;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * A scheduler that already provides a {@link ScheduledExecutorService} for scheduling events to run at a specific
 * point in (real) time.
 * Starting and stopping the executor as well as adding/removing schedules is already implemented.
 *
 * @param <S> Type of Schedule
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@CustomLog(topic = "Schedules")
public abstract class ExecutorServiceScheduler<S extends Schedule> extends Scheduler<S> {

    /**
     * Maximum time that the scheduler will wait on shutdown/reload for currently executing schedules.
     */
    @VisibleForTesting
    static final int TERMINATION_TIMEOUT_MS = 5;

    /**
     * Supplier used to create the {@link #executor}.
     */
    private final Supplier<ScheduledExecutorService> executorServiceSupplier;

    /**
     * Executor service that can be used to run code at a specific time in the future.
     */
    protected ScheduledExecutorService executor;

    /**
     * Constructor to create a new instance of this scheduler.
     */
    public ExecutorServiceScheduler() {
        this(Executors::newSingleThreadScheduledExecutor);
    }

    /**
     * Constructor to create a new instance of this scheduler with a custom executor.
     *
     * @param executor supplier used to create new instances of the executor used by this scheduler
     */
    public ExecutorServiceScheduler(final Supplier<ScheduledExecutorService> executor) {
        super();
        this.executorServiceSupplier = executor;
    }

    /**
     * <p>
     * Start all schedules that have been added to this scheduler.
     * This method is called on startup and reload of BetonQuest to activate/resume all schedules.
     * </p>
     * <p>
     * Override this method to handle catching up schedules that were missed during reloading or shutdown of the server,
     * based on their {@link CatchupStrategy}.
     * </p>
     * <p><b>
     * Make sure to call {@code super.start()}, otherwise the executor will not be instantiated.
     * </b></p>
     */
    @Override
    public void start() {
        super.start();
        executor = executorServiceSupplier.get();
        schedules.values().forEach(this::schedule);
    }

    /**
     * <p>
     * Method that takes a registered schedule and tells the executor when and how to run it.
     * See {@link RealtimeDailyScheduler} for an example how to implement.
     * </p>
     * <p><b>
     * Do not confuse this with {@link #addSchedule(Schedule)}, which does only add a schedule to the list of registered
     * schedules.
     * </b></p>
     *
     * @param schedule a schedule from {@link #schedules} map
     */
    protected abstract void schedule(S schedule);

    /**
     * <p>
     * Stop the scheduler and unregister all schedules that belong to this scheduler.
     * Typically this method is called on reload and server shutdown.
     * </p>
     * <p><b>
     * When overriding this method, make sure to call {@code super.stop()} at some point to remove schedules
     * from the executor and clear the map of schedules.
     * </b></p>
     */
    @Override
    public void stop() {
        if (isRunning()) {
            LOG.debug("Stopping " + getClass().getSimpleName().toLowerCase(Locale.ROOT).replace("scheduler", "")
                    + " scheduler.");
            executor.shutdownNow();
            try {
                final boolean terminated = executor.awaitTermination(TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (!terminated) {
                    throw new TimeoutException("Not all schedules could be terminated within time constraints");
                }
                LOG.debug("Successfully shut down executor service.");
            } catch (final InterruptedException | TimeoutException e) {
                LOG.error("Error while stopping scheduler", e);
            }
            super.stop();
            LOG.debug("Stop complete.");
        }
    }
}
