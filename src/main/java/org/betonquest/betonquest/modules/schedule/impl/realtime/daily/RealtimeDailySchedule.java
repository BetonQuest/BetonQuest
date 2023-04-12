package org.betonquest.betonquest.modules.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A schedule that mimics the functionality and style of the old static event system.
 * Time is just {@code HH:mm} format (e.g. 14:45)
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
     * @throws InstructionParseException if parsing the config failed
     */
    public RealtimeDailySchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
        super(scheduleID, instruction);
        try {
            this.timeToRun = LocalTime.parse(super.time, TIME_FORMAT);
        } catch (final DateTimeParseException e) {
            throw new InstructionParseException("Unable to parse time '" + super.time + "': " + e.getMessage(), e);
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
     * Get the next execution time as instant.
     *
     * @return instant when the next run of this schedule will be
     */
    public Instant getNextExecution() {
        final OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime targetTime = getTimeToRun().atOffset(now.getOffset()).atDate(now.toLocalDate());
        if (targetTime.isBefore(now)) {
            targetTime = targetTime.plusDays(1);
        }
        return targetTime.toInstant();
    }
}
