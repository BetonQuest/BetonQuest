package org.betonquest.betonquest.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Factory to create {@link RealtimeDailySchedule}s.
 */
public class RealtimeDailyScheduleFactory extends BaseScheduleFactory<RealtimeDailySchedule> {

    /**
     * The DateTimeFormatter used for parsing the time strings.
     */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Create a new Realtime Schedule Factory.
     */
    public RealtimeDailyScheduleFactory() {
        super();
    }

    @Override
    public RealtimeDailySchedule createNewInstance(final ScheduleIdentifier scheduleID, final SectionInstruction instruction)
            throws QuestException {
        final ScheduleData scheduleData = parseScheduleData(instruction);
        final String time = scheduleData.time();
        final LocalTime localTime;
        try {
            localTime = LocalTime.parse(time, TIME_FORMAT);
        } catch (final DateTimeParseException e) {
            throw new QuestException("Unable to parse time '" + time + "': " + e.getMessage(), e);
        }
        return new RealtimeDailySchedule(scheduleID, scheduleData.actions(), scheduleData.catchup(), localTime);
    }
}
