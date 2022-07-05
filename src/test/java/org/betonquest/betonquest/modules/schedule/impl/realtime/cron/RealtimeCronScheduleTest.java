package org.betonquest.betonquest.modules.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.schedule.CronRebootScheduleTest;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Tests for realtime cron schedule.
 */
public class RealtimeCronScheduleTest extends CronRebootScheduleTest {

    @Override
    protected RealtimeCronSchedule createSchedule() throws InstructionParseException {
        return new RealtimeCronSchedule(scheduleID, section);
    }
}
