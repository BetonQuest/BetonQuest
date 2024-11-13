package org.betonquest.betonquest.modules.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.impl.ExecutorServiceScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * The scheduler for {@link RealtimeDailySchedule}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class RealtimeDailyScheduler extends ExecutorServiceScheduler<RealtimeDailySchedule, Instant> {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * A cache where the last execution times of a schedule are stored.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Create a new simple scheduler and pass BetonQuest instance to it.
     *
     * @param log                the logger that will be used for logging
     * @param executor           supplier used to create new instances of the executor used by this scheduler
     * @param lastExecutionCache cache where the last execution times of a schedule are stored
     */
    public RealtimeDailyScheduler(final BetonQuestLogger log, final Supplier<ScheduledExecutorService> executor, final LastExecutionCache lastExecutionCache) {
        super(log, executor);
        this.log = log;
        this.lastExecutionCache = lastExecutionCache;
    }

    /**
     * Create a new simple scheduler and pass BetonQuest instance to it.
     *
     * @param log                the logger that will be used for logging
     * @param lastExecutionCache cache where the last execution times of a schedule are stored
     */
    public RealtimeDailyScheduler(final BetonQuestLogger log, final LastExecutionCache lastExecutionCache) {
        super(log);
        this.log = log;
        this.lastExecutionCache = lastExecutionCache;
    }

    @Override
    public void start(final Instant now) {
        lastExecutionCache.cacheStartupTime(now, schedules.keySet());
        log.debug("Starting simple scheduler.");
        catchupMissedSchedules(now);
        super.start(now);
        log.debug("Simple scheduler start complete.");
    }

    @Override
    protected Instant getNow() {
        return Instant.now();
    }

    /**
     * Search for missed schedule runs during shutdown and rerun them if the catchup strategy says so.
     * The method should guarantee that the schedules are executed in the order they would have occurred.
     *
     * @param now The Instant of now
     */
    private void catchupMissedSchedules(final Instant now) {
        log.debug("Collecting missed schedules...");
        final List<RealtimeDailySchedule> missedSchedules = listMissedSchedules(now);
        log.debug("Found " + missedSchedules.size() + " missed schedule runs that will be caught up.");
        if (!missedSchedules.isEmpty()) {
            log.debug("Running missed schedules to catch up...");
            for (final RealtimeDailySchedule schedule : missedSchedules) {
                lastExecutionCache.cacheExecutionTime(now, schedule.getId());
                executeEvents(schedule);
            }
        }
    }

    /**
     * <p>
     * This method creates a list of schedules that should be run to catch up any missed schedules.
     * The list is in the same order that the schedules would have run, had they not been missed.
     * Schedules with {@link CatchupStrategy#ONE} are in the list only once, while schedules with
     * {@link CatchupStrategy#ALL} are there as often as they have been missed.
     * </p>
     * <p>
     * Uses the Queue returned by {@link #oldestMissedRuns(Instant)} to order missed runs.
     * Each runs schedule will be added to the missed schedules list.
     * </p>
     * <p>
     * For all schedules with catchup strategy {@code ALL} the method will determine the next execution time
     * after that oldest run.
     * If that time is in the past it will be added to the queue of missed runs so it will again be used to check if
     * a newer missed run exists.
     * This loops till all missed runs have been processed to make sure all possible execution times till now have been
     * added.
     * </p>
     *
     * @param now The Instant of now
     * @return list of schedules that should be run to catch up any missed schedules
     */
    private List<RealtimeDailySchedule> listMissedSchedules(final Instant now) {
        final List<RealtimeDailySchedule> missed = new ArrayList<>();
        final Queue<MissedRun> missedRuns = oldestMissedRuns(now);

        while (!missedRuns.isEmpty()) {
            final MissedRun earliest = missedRuns.poll();
            missed.add(earliest.schedule);
            log.debug(earliest.schedule.getId().getPackage(),
                    "Schedule '" + earliest.schedule.getId() + "' run missed at " + earliest.runTime);
            if (earliest.schedule.getCatchup() == CatchupStrategy.ALL) {
                final Instant nextExecution = earliest.runTime.plus(1, ChronoUnit.DAYS);
                if (nextExecution.isBefore(now)) {
                    missedRuns.add(new MissedRun(earliest.schedule, nextExecution));
                }
            }
        }
        return missed;
    }

    /**
     * This method returns the first missed run of each schedule (if a run was missed).
     * The returned queue is ordered by the time when the schedule should have run, in ascending order.
     *
     * @param now The Instant of now
     * @return queue of missed runs, sorted from old to now
     */
    private Queue<MissedRun> oldestMissedRuns(final Instant now) {
        final Queue<MissedRun> missedRuns = new PriorityQueue<>(schedules.size() + 1, Comparator.comparing(MissedRun::runTime));
        for (final RealtimeDailySchedule schedule : schedules.values()) {
            if (schedule.getCatchup() != CatchupStrategy.NONE) {
                final Optional<Instant> lastExecutionTime = lastExecutionCache.getLastExecutionTime(schedule.getId());
                final Optional<Instant> nextExecution = lastExecutionTime.map(schedule::getNextExecution);
                if (nextExecution.isPresent() && nextExecution.get().isBefore(now)) {
                    missedRuns.add(new MissedRun(schedule, nextExecution.get()));
                }
            }
        }
        return missedRuns;
    }

    @Override
    protected void schedule(final Instant now, final RealtimeDailySchedule schedule) {
        final Instant nextExecution = schedule.getNextExecution(now);
        executor.schedule(() -> {
            lastExecutionCache.cacheExecutionTime(nextExecution, schedule.getId());
            executeEvents(schedule);
            schedule(nextExecution, schedule);
        }, ChronoUnit.MILLIS.between(now, nextExecution), TimeUnit.MILLISECONDS);
    }

    /**
     * Helper class representing a single run of a schedule that was missed.
     *
     * @param schedule the schedule to which the missed run belongs
     * @param runTime  the time when the missed run should have taken place.
     */
    private record MissedRun(RealtimeDailySchedule schedule, Instant runTime) {
    }
}
