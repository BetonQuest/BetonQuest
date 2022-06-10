package org.betonquest.betonquest.modules.schedule.impl.realtime;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.impl.ExecutorServiceScheduler;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

/**
 * The scheduler for {@link RealtimeSchedule}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@CustomLog(topic = "Schedules")
public class RealtimeScheduler extends ExecutorServiceScheduler<RealtimeSchedule> {

    /**
     * A cache where the last execution times of a schedule are stored.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Flag that states if this start is a reboot (true) or only a reload (false)
     */
    private boolean reboot = true;

    /**
     * Create a new realtime scheduler and pass BetonQuest instance to it
     *
     * @param betonQuestInstance BetonQuest instance
     */
    public RealtimeScheduler(final BetonQuest betonQuestInstance) {
        super(betonQuestInstance);
        this.lastExecutionCache = betonQuestInstance.getLastExecutionCache();
    }

    @Override
    public void start() {
        super.start();
        if (reboot) {
            reboot = false;
            runRebootSchedules();
        }
        catchupMissedSchedules();
    }

    /**
     * Run schedules with '@reboot' time instruction on reboot.
     */
    private void runRebootSchedules() {
        final List<RealtimeSchedule> rebootSchedules = schedules.values().stream()
                .filter(CronSchedule::shouldRunOnReboot).toList();
        if (!rebootSchedules.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(betonQuestInstance, () -> rebootSchedules.forEach(schedule -> {
                for (final EventID eventID : schedule.getEvents()) {
                    BetonQuest.event(null, eventID);
                }
            }), 1L);
        }
    }

    /**
     * Search for missed schedule runs during shutdown and rerun them if the catchup strategy says so.
     * The method should guarantee that the schedules are executed in the order they would have occurred.
     */
    private void catchupMissedSchedules() {
        final List<RealtimeSchedule> missedSchedules = listMissedSchedules();
        if (!missedSchedules.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(betonQuestInstance, () -> missedSchedules.forEach(missed -> {
                lastExecutionCache.cacheExecutionTime(missed.getId(), Instant.now());
                for (final EventID eventID : missed.getEvents()) {
                    BetonQuest.event(null, eventID);
                }
            }), 1L);
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
    private List<RealtimeSchedule> listMissedSchedules() {
        final List<RealtimeSchedule> missed = new ArrayList<>();

        final PriorityQueue<MissedRun> missedRuns = oldestMissedRuns();

        while (!missedRuns.isEmpty()) {
            final MissedRun earliest = missedRuns.poll();
            missed.add(earliest.schedule);
            if (earliest.schedule.getCatchup() == CatchupStrategy.ALL) {
                final Optional<ZonedDateTime> nextExecution = earliest.schedule.getExecutionTime().nextExecution(earliest.runTime);
                if (nextExecution.isPresent() && nextExecution.get().isBefore(ZonedDateTime.now())) {
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
     * @return priority que of missed runs, sorted from old to now
     */
    private PriorityQueue<MissedRun> oldestMissedRuns() {
        final PriorityQueue<MissedRun> missedRuns = new PriorityQueue<>(schedules.size() + 1, Comparator.comparing(MissedRun::runTime));
        for (final RealtimeSchedule schedule : schedules.values()) {
            if (schedule.getCatchup() != CatchupStrategy.NONE) {
                final Optional<ZonedDateTime> cachedExecutionTime = lastExecutionCache.getLastExecutionTime(schedule.getId())
                        .map(cachedTime -> cachedTime.atZone(ZoneId.systemDefault()));
                final Optional<ZonedDateTime> nextExecution = cachedExecutionTime
                        .flatMap(cachedTime -> schedule.getExecutionTime().nextExecution(cachedTime));
                if (nextExecution.isPresent() && nextExecution.get().isBefore(ZonedDateTime.now())) {
                    missedRuns.add(new MissedRun(schedule, nextExecution.get()));
                }
            }
        }
        return missedRuns;
    }

    @Override
    protected void schedule(final RealtimeSchedule schedule) {
        schedule.getExecutionTime().timeToNextExecution(ZonedDateTime.now()).ifPresent(durationToNextRun ->
                executor.schedule(() -> {
                    lastExecutionCache.cacheExecutionTime(schedule.getId(), Instant.now());
                    executeEvents(schedule);
                    schedule(schedule);
                }, durationToNextRun.toMillis(), TimeUnit.MILLISECONDS));
    }

    /**
     * Helper class representing a single run of a schedule that was missed.
     *
     * @param schedule the schedule to which the missed run belongs
     * @param runTime  the time when the missed run should have taken place.
     */
    record MissedRun(RealtimeSchedule schedule, ZonedDateTime runTime) {
    }
}
