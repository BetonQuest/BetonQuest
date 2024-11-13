package org.betonquest.betonquest.modules.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.impl.ExecutorServiceScheduler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
 * The scheduler for {@link RealtimeCronSchedule}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class RealtimeCronScheduler extends ExecutorServiceScheduler<RealtimeCronSchedule, Instant> {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * A cache where the last execution times of a schedule are stored.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Flag that states if this start is a reboot (true) or only a reload (false).
     */
    private boolean reboot = true;

    /**
     * Create a new realtime scheduler and pass BetonQuest instance to it.
     *
     * @param log                the logger that will be used for logging
     * @param lastExecutionCache cache where the last execution times of a schedule are stored
     */
    public RealtimeCronScheduler(final BetonQuestLogger log, final LastExecutionCache lastExecutionCache) {
        super(log);
        this.log = log;
        this.lastExecutionCache = lastExecutionCache;
    }

    /**
     * Create a new realtime scheduler and pass BetonQuest instance to it.
     *
     * @param log                the logger that will be used for logging
     * @param executor           supplier used to create new instances of the executor used by this scheduler
     * @param lastExecutionCache cache where the last execution times of a schedule are stored
     */
    public RealtimeCronScheduler(final BetonQuestLogger log, final Supplier<ScheduledExecutorService> executor, final LastExecutionCache lastExecutionCache) {
        super(log, executor);
        this.log = log;
        this.lastExecutionCache = lastExecutionCache;
    }

    @Override
    public void start(final Instant now) {
        lastExecutionCache.cacheStartupTime(now, schedules.keySet());
        log.debug("Starting realtime scheduler.");
        if (reboot) {
            reboot = false;
            runRebootSchedules();
        }
        catchupMissedSchedules(now);
        super.start(now);
        log.debug("Realtime scheduler start complete.");
    }

    @Override
    protected Instant getNow() {
        return Instant.now();
    }

    /**
     * Run schedules with '@reboot' time instruction on reboot.
     */
    private void runRebootSchedules() {
        log.debug("Collecting reboot schedules...");
        final List<RealtimeCronSchedule> rebootSchedules = schedules.values().stream()
                .filter(CronSchedule::shouldRunOnReboot).toList();
        log.debug("Found " + rebootSchedules.size() + " reboot schedules. They will be run on next server tick.");
        rebootSchedules.forEach(this::executeEvents);
    }

    /**
     * Search for missed schedule runs during shutdown and rerun them if the catchup strategy says so.
     * The method should guarantee that the schedules are executed in the order they would have occurred.
     *
     * @param now The Instant of now
     */
    private void catchupMissedSchedules(final Instant now) {
        final List<RealtimeCronSchedule> missedSchedules = listMissedSchedules(now);
        log.debug("Found " + missedSchedules.size() + " missed schedule runs that will be caught up.");
        if (!missedSchedules.isEmpty()) {
            log.debug("Running missed schedules to catch up...");
            for (final RealtimeCronSchedule missed : missedSchedules) {
                lastExecutionCache.cacheExecutionTime(now, missed.getId());
                executeEvents(missed);
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
    private List<RealtimeCronSchedule> listMissedSchedules(final Instant now) {
        final List<RealtimeCronSchedule> missed = new ArrayList<>();

        final Queue<MissedRun> missedRuns = oldestMissedRuns(now);

        while (!missedRuns.isEmpty()) {
            final MissedRun earliest = missedRuns.poll();
            missed.add(earliest.schedule);
            log.debug(earliest.schedule.getId().getPackage(),
                    "Schedule '" + earliest.schedule.getId() + "' run missed at " + earliest.runTime);
            if (earliest.schedule.getCatchup() == CatchupStrategy.ALL) {
                final Optional<ZonedDateTime> nextExecution = earliest.schedule.getExecutionTime().nextExecution(earliest.runTime);
                if (nextExecution.isPresent() && nextExecution.get().isBefore(ZonedDateTime.ofInstant(now, ZoneId.systemDefault()))) {
                    missedRuns.add(new MissedRun(earliest.schedule, nextExecution.get()));
                }
            }
        }
        return missed;
    }

    /**
     * <p>
     * This method returns the first missed run of each schedule (if a run was missed).
     * The returned queue is ordered by the time when the schedule should have run, in ascending order.
     * </p>
     * <p>
     * To find the oldest missed run it looks up the cached last execution time of each schedule.
     * Then the next execution time after that cached time will be calculated.
     * If that next execution time is in the past, it will be added to the que of missed runs.
     * Schedules with {@link CatchupStrategy#NONE} will be ignored.
     * </p>
     *
     * @param now The Instant of now
     * @return priority que of missed runs, sorted from old to now
     */
    private Queue<MissedRun> oldestMissedRuns(final Instant now) {
        final Queue<MissedRun> missedRuns = new PriorityQueue<>(schedules.size() + 1, Comparator.comparing(MissedRun::runTime));
        for (final RealtimeCronSchedule schedule : schedules.values()) {
            if (schedule.getCatchup() != CatchupStrategy.NONE) {
                final Optional<ZonedDateTime> cachedExecutionTime = lastExecutionCache.getLastExecutionTime(schedule.getId())
                        .map(cachedTime -> cachedTime.atZone(ZoneId.systemDefault()));
                final Optional<ZonedDateTime> nextExecution = cachedExecutionTime
                        .flatMap(cachedTime -> schedule.getExecutionTime().nextExecution(cachedTime));
                if (nextExecution.isPresent() && nextExecution.get().isBefore(ZonedDateTime.ofInstant(now, ZoneId.systemDefault()))) {
                    missedRuns.add(new MissedRun(schedule, nextExecution.get()));
                }
            }
        }
        return missedRuns;
    }

    @Override
    protected void schedule(final Instant now, final RealtimeCronSchedule schedule) {
        schedule.getExecutionTime().timeToNextExecution(ZonedDateTime.ofInstant(now, ZoneId.systemDefault())).ifPresent(durationToNextRun ->
                executor.schedule(() -> {
                    final Instant nextExecution = now.plus(durationToNextRun);
                    lastExecutionCache.cacheExecutionTime(nextExecution, schedule.getId());
                    executeEvents(schedule);
                    schedule(nextExecution, schedule);
                }, durationToNextRun.toMillis(), TimeUnit.MILLISECONDS));
    }

    /**
     * Helper class representing a single run of a schedule that was missed.
     *
     * @param schedule the schedule to which the missed run belongs
     * @param runTime  the time when the missed run should have taken place.
     */
    /* default */ record MissedRun(RealtimeCronSchedule schedule, ZonedDateTime runTime) {
    }
}
