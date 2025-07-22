package org.betonquest.betonquest.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.schedule.CronRebootScheduleTest;

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
