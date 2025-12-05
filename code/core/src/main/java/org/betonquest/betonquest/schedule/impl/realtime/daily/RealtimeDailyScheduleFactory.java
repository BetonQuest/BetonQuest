package org.betonquest.betonquest.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.configuration.ConfigurationSection;

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
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     */
    public RealtimeDailyScheduleFactory(final Variables variables, final QuestPackageManager packManager) {
        super(variables, packManager);
    }

    @Override
    public RealtimeDailySchedule createNewInstance(final ScheduleID scheduleID, final ConfigurationSection config)
            throws QuestException {
        final ScheduleData scheduleData = parseScheduleData(scheduleID.getPackage(), config);
        final String time = scheduleData.time();
        final LocalTime localTime;
        try {
            localTime = LocalTime.parse(time, TIME_FORMAT);
        } catch (final DateTimeParseException e) {
            throw new QuestException("Unable to parse time '" + time + "': " + e.getMessage(), e);
        }
        return new RealtimeDailySchedule(scheduleID, scheduleData.events(), scheduleData.catchup(), localTime);
    }
}
