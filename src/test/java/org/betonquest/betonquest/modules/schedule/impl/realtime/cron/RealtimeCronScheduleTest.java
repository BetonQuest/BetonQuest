package org.betonquest.betonquest.modules.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.schedule.CronRebootScheduleTest;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Tests for realtime cron schedule.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class RealtimeCronScheduleTest extends CronRebootScheduleTest {

    @Override
    protected RealtimeCronSchedule createSchedule() throws QuestException {
        return new RealtimeCronSchedule(scheduleID, section);
    }
}
