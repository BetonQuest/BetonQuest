package org.betonquest.betonquest.modules.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A schedule that mimics the functionality and style of the old static event system.
 * Time is just {@code HH:mm} format (e.g., 14:45)
 * and defines the time of day when the events from this schedule will be run.
 */
public class RealtimeDailySchedule extends Schedule {

    /**
     * The DateTimeFormatter used for parsing the time strings.
     */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Parsed time of day when the events should run.
     */
    private final LocalTime timeToRun;

    /**
     * Creates new instance of the schedule.
     *
     * @param scheduleID  id of the new schedule
     * @param instruction config defining the schedule
     * @throws QuestException if parsing the config failed
     */
    public RealtimeDailySchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws QuestException {
        super(scheduleID, instruction);
        try {
            this.timeToRun = LocalTime.parse(super.time, TIME_FORMAT);
        } catch (final DateTimeParseException e) {
            throw new QuestException("Unable to parse time '" + super.time + "': " + e.getMessage(), e);
        }
    }

    /**
     * Get the time of day when the events should run.
     *
     * @return the local time to run the events
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
