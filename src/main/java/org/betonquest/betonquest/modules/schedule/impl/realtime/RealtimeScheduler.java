package org.betonquest.betonquest.modules.schedule.impl.realtime;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.modules.schedule.impl.ExecutorServiceScheduler;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * The scheduler for {@link RealtimeSchedule}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@CustomLog(topic = "Schedules")
public class RealtimeScheduler extends ExecutorServiceScheduler<RealtimeSchedule> {

    /**
     * Create a new realtime scheduler and pass BetonQuest instance to it
     *
     * @param betonQuestInstance BetonQuest instance
     */
    public RealtimeScheduler(final BetonQuest betonQuestInstance) {
        super(betonQuestInstance);
    }

    //TODO: catch up missed schedules

    @Override
    protected void schedule(final RealtimeSchedule schedule) {
        schedule.getExecutionTime().timeToNextExecution(ZonedDateTime.now()).ifPresent(durationToNextRun -> {
            executor.schedule(() -> {
                executeEvents(schedule);
                schedule(schedule);
            }, durationToNextRun.toMillis(), TimeUnit.MILLISECONDS);
        });
    }

}
