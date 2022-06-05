package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.id.ScheduleID;

import java.util.HashMap;
import java.util.Map;

/**
 * Superclass of all event schedulers.
 * While {@link Schedule} holds the data and settings of a schedule, children of this class should contain the logic for
 * scheduling of events.
 * Once a time defined in the schedule is met, the referenced events shall be executed using {@link FIREEVENTS}.
 * Also, this class should implement the {@link CatchupStrategy} required by the schedule.
 *
 * @param <S> Type of Schedule
 */
public abstract class Scheduler<S extends Schedule> {

    /**
     * Map containing all schedules that belong to this scheduler
     */
    protected final Map<ScheduleID, S> schedules = new HashMap<>();
}
