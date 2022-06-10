package org.betonquest.betonquest.modules.schedule.impl.simple;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.impl.ExecutorServiceScheduler;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

/**
 * The scheduler for {@link SimpleSchedule}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@CustomLog(topic = "Schedules")
public class SimpleScheduler extends ExecutorServiceScheduler<SimpleSchedule> {

    /**
     * A cache where the last execution times of a schedule are stored.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Create a new simple scheduler and pass BetonQuest instance to it.
     *
     * @param betonQuestInstance BetonQuest instance
     */
    public SimpleScheduler(final BetonQuest betonQuestInstance) {
        super(betonQuestInstance);
        this.lastExecutionCache = betonQuestInstance.getLastExecutionCache();
    }

    @Override
    public void start() {
        super.start();
        catchupMissedSchedules();
    }

    /**
     * Search for missed schedule runs during shutdown and rerun them if the catchup strategy says so.
     * The method should guarantee that the schedules are executed in the order they would have occurred.
     */
    private void catchupMissedSchedules() {
        final List<SimpleSchedule> missedSchedules = listMissedSchedules();
        if (!missedSchedules.isEmpty()) {
            //fixme has same limitations as realtime schedule
            Bukkit.getScheduler().runTaskLater(betonQuestInstance, () -> {
                for (final SimpleSchedule schedule : missedSchedules) {
                    lastExecutionCache.cacheExecutionTime(schedule.getId(), Instant.now());
                    for (final EventID event : schedule.getEvents()) {
                        BetonQuest.event(null, event);
                    }
                }
            }, 1L);
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
    private List<SimpleSchedule> listMissedSchedules() {
        final List<SimpleSchedule> missed = new ArrayList<>();
        final PriorityQueue<MissedRun> missedRuns = oldestMissedRuns();

        while (!missedRuns.isEmpty()) {
            final MissedRun earliest = missedRuns.poll();
            missed.add(earliest.schedule);
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
        for (final SimpleSchedule schedule : schedules.values()) {
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
    protected void schedule(final SimpleSchedule schedule) {
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
    private record MissedRun(SimpleSchedule schedule, Instant runTime) {
    }
}
