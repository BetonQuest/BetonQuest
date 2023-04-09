package org.betonquest.betonquest.modules.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.BetonQuestLogger;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * The scheduler for {@link RealtimeDailySchedule}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class RealtimeDailyScheduler extends ExecutorServiceScheduler<RealtimeDailySchedule> {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(RealtimeDailyScheduler.class, "Schedules");

    /**
     * A cache where the last execution times of a schedule are stored.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Create a new simple scheduler and pass BetonQuest instance to it.
     *
     * @param executor           supplier used to create new instances of the executor used by this scheduler
     * @param lastExecutionCache cache where the last execution times of a schedule are stored
     */
    public RealtimeDailyScheduler(final Supplier<ScheduledExecutorService> executor, final LastExecutionCache lastExecutionCache) {
        super(executor);
        this.lastExecutionCache = lastExecutionCache;
    }

    /**
     * Create a new simple scheduler and pass BetonQuest instance to it.
     *
     * @param lastExecutionCache cache where the last execution times of a schedule are stored
     */
    public RealtimeDailyScheduler(final LastExecutionCache lastExecutionCache) {
        super();
        this.lastExecutionCache = lastExecutionCache;
    }

    @Override
    public void start() {
        LOG.debug("Starting simple scheduler.");
        catchupMissedSchedules();
        super.start();
        LOG.debug("Simple scheduler start complete.");
    }

    /**
     * Search for missed schedule runs during shutdown and rerun them if the catchup strategy says so.
     * The method should guarantee that the schedules are executed in the order they would have occurred.
     */
    private void catchupMissedSchedules() {
        LOG.debug("Collecting missed schedules...");
        final List<RealtimeDailySchedule> missedSchedules = listMissedSchedules();
        LOG.debug("Found " + missedSchedules.size() + " missed schedule runs that will be caught up.");
        if (!missedSchedules.isEmpty()) {
            LOG.debug("Running missed schedules to catch up...");
            for (final RealtimeDailySchedule schedule : missedSchedules) {
                lastExecutionCache.cacheExecutionTime(schedule.getId(), Instant.now());
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
     * Uses the Queue returned by {@link #oldestMissedRuns()} to order missed runs.
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
     * @return list of schedules that should be run to catch up any missed schedules
     */
    private List<RealtimeDailySchedule> listMissedSchedules() {
        final List<RealtimeDailySchedule> missed = new ArrayList<>();
        final PriorityQueue<MissedRun> missedRuns = oldestMissedRuns();

        while (!missedRuns.isEmpty()) {
            final MissedRun earliest = missedRuns.poll();
            missed.add(earliest.schedule);
            LOG.debug(earliest.schedule.getId().getPackage(),
                    "Schedule '" + earliest.schedule.getId() + "' run missed at " + earliest.runTime);
            if (earliest.schedule.getCatchup() == CatchupStrategy.ALL) {
                final Instant nextExecution = earliest.runTime.plus(1, ChronoUnit.DAYS);
                if (nextExecution.isBefore(Instant.now())) {
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
     * @return queue of missed runs, sorted from old to now
     */
    private PriorityQueue<MissedRun> oldestMissedRuns() {
        final PriorityQueue<MissedRun> missedRuns = new PriorityQueue<>(schedules.size() + 1, Comparator.comparing(MissedRun::runTime));
        for (final RealtimeDailySchedule schedule : schedules.values()) {
            if (schedule.getCatchup() != CatchupStrategy.NONE) {
                final Optional<Instant> lastExecutionTime = lastExecutionCache.getLastExecutionTime(schedule.getId());
                if (lastExecutionTime.isPresent()
                        && lastExecutionTime.get().plus(1, ChronoUnit.DAYS).isBefore(Instant.now())) {
                    missedRuns.add(new MissedRun(schedule, lastExecutionTime.get().plus(1, ChronoUnit.DAYS)));
                }
            }
        }
        return missedRuns;
    }


    @Override
    protected void schedule(final RealtimeDailySchedule schedule) {
        executor.schedule(() -> {
            lastExecutionCache.cacheExecutionTime(schedule.getId(), Instant.now());
            executeEvents(schedule);
            schedule(schedule);
        }, Instant.now().until(schedule.getNextExecution(), ChronoUnit.MILLIS), TimeUnit.MILLISECONDS);
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
