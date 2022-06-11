package org.betonquest.betonquest.modules.schedule.impl.realtime;

import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Schedules events to occur at a specific real time, using cron syntax.
 */
public class RealtimeSchedule extends CronSchedule {

    /**
     * Creates a new instance of the schedule.
     *
     * @param scheduleID  id of the new schedule
     * @param instruction config defining the schedule
     * @throws InstructionParseException if parsing the config failed
     */
    public RealtimeSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
        super(scheduleID, instruction, DEFAULT_CRON_DEFINITION, true);
    }
}
