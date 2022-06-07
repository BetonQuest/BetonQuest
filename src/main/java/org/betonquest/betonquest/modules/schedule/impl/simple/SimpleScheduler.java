package org.betonquest.betonquest.modules.schedule.impl.simple;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.modules.schedule.impl.ExecutorServiceScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * The scheduler for {@link SimpleSchedule}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@CustomLog(topic = "Schedules")
public class SimpleScheduler extends ExecutorServiceScheduler<SimpleSchedule> {

    /**
     * Create a new simple scheduler and pass BetonQuest instance to it.
     *
     * @param betonQuestInstance BetonQuest instance
     */
    public SimpleScheduler(final BetonQuest betonQuestInstance) {
        super(betonQuestInstance);
    }

    //TODO: catch up missed schedules

    @Override
    protected void schedule(final SimpleSchedule schedule) {
        executor.schedule(() -> {
            executeEvents(schedule);
            schedule(schedule);
        }, Instant.now().until(schedule.getNextExecution(), ChronoUnit.MILLIS), TimeUnit.MILLISECONDS);
    }
}
