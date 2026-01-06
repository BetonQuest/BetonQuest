package org.betonquest.betonquest.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * A schedule that mimics the functionality and style of the old static action system.
 * Time is just {@code HH:mm} format (e.g., 14:45)
 * and defines the time of day when the actions from this schedule will be run.
 */
public class RealtimeDailySchedule extends Schedule {

    /**
     * Parsed time of day when the actions should run.
     */
    private final LocalTime timeToRun;

    /**
     * Creates new instance of the schedule.
     *
     * @param scheduleID the schedule id
     * @param actions    the actions to execute
     * @param catchup    the catchup strategy
     * @param timeToRun  the resolved time to run the schedule
     */
    public RealtimeDailySchedule(final ScheduleID scheduleID, final List<ActionID> actions, final CatchupStrategy catchup,
                                 final LocalTime timeToRun) {
        super(scheduleID, actions, catchup);
        this.timeToRun = timeToRun;
    }

    /**
     * Get the time of day when the actions should run.
     *
     * @return the local time to run the actions
     */
    public LocalTime getTimeToRun() {
        return timeToRun;
    }

    /**
     * Get the next execution time as an instant.
     *
     * @param startTime the time to start searching for the next execution
     * @return instant when the next run of this schedule will be
     */
    public Instant getNextExecution(final Instant startTime) {
        final OffsetDateTime now = OffsetDateTime.ofInstant(startTime, ZoneId.systemDefault());
        OffsetDateTime targetTime = OffsetDateTime.of(now.toLocalDate(), getTimeToRun(), now.getOffset());
        if (!targetTime.isAfter(now)) {
            targetTime = targetTime.plusDays(1);
        }
        return targetTime.toInstant();
    }
}
