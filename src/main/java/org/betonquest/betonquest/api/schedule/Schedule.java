package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.quest.event.EventID;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * A Schedule may be defined in any package.
 * It allows scheduling events to run for all online players at specific times.
 * </p>
 *
 * <p>
 * All types of Schedules must extend this superclass.
 * It should only be responsible for holding the data &amp; options of a single schedule.
 * The actual scheduling logic should be defined by extending {@link Scheduler}
 * </p>
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class Schedule {

    /**
     * Identifier of this schedule.
     */
    protected final ScheduleID scheduleID;

    /**
     * A list of events that will be run by this schedule.
     */
    protected final List<EventID> events;

    /**
     * Defines how the scheduler should behave if an execution of the schedule was missed
     * (e.g., due to a shutdown of the server).
     * Should be None by default.
     */
    protected final CatchupStrategy catchup;

    /**
     * Creates new instance of the schedule.
     *
     * @param scheduleID the schedule id
     * @param events     the events to execute
     * @param catchup    the catchup strategy
     */
    public Schedule(final ScheduleID scheduleID, final List<EventID> events, final CatchupStrategy catchup) {
        this.scheduleID = scheduleID;
        this.events = Collections.unmodifiableList(events);
        this.catchup = catchup;
    }

    /**
     * Get the Identifier of this schedule.
     *
     * @return the id
     */
    public ScheduleID getId() {
        return scheduleID;
    }

    /**
     * Get a list of events that will be run by this schedule.
     *
     * @return unmodifiable list of events
     */
    public List<EventID> getEvents() {
        return events;
    }

    /**
     * Get how the scheduler should behave if an execution of the schedule was missed
     * (e.g., due to a shutdown of the server).
     *
     * @return the catchup strategy, {@link CatchupStrategy#NONE} by default
     */
    public CatchupStrategy getCatchup() {
        return catchup;
    }
}
