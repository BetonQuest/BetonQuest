package org.betonquest.betonquest.modules.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Schedules events to occur at a specific real time, using cron syntax.
 */
public class RealtimeCronSchedule extends CronSchedule {

    /**
     * Creates a new instance of the schedule.
     *
     * @param scheduleID  id of the new schedule
     * @param instruction config defining the schedule
     * @throws QuestException if parsing the config failed
     */
    public RealtimeCronSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws QuestException {
        super(scheduleID, instruction, REBOOT_CRON_DEFINITION);
    }
}
