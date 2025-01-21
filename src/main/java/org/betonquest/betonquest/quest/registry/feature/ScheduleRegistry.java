package org.betonquest.betonquest.quest.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.quest.registry.FactoryRegistry;
import org.betonquest.betonquest.schedule.EventScheduling;

import java.util.Set;

/**
 * Registry for usable schedule type in the Event scheduler.
 */
public class ScheduleRegistry extends FactoryRegistry<EventScheduling.ScheduleType<?, ?>> {

    /**
     * Create a new type registry.
     *
     * @param log the logger that will be used for logging
     */
    public ScheduleRegistry(final BetonQuestLogger log) {
        super(log, "Scheduler");
    }

    /**
     * Register a new schedule type.
     *
     * @param name      name of the schedule type
     * @param schedule  class object of the schedule type
     * @param scheduler instance of the scheduler
     * @param <S>       type of schedule
     */
    public <S extends Schedule> void register(final String name, final Class<S> schedule, final Scheduler<S, ?> scheduler) {
        register(name, new EventScheduling.ScheduleType<>(schedule, scheduler));
    }

    /**
     * Get all registered Schedule types.
     *
     * @return an unmodifiable copy
     */
    public Set<EventScheduling.ScheduleType<?, ?>> values() {
        return Set.copyOf(types.values());
    }
}
