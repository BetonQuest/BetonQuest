package org.betonquest.betonquest.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.schedule.CronRebootScheduleTest;
import org.betonquest.betonquest.api.schedule.CronSchedule;

/**
 * Tests for realtime cron schedule.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class RealtimeCronScheduleTest extends CronRebootScheduleTest {

    @Override
    protected CronSchedule createSchedule() throws QuestException {
        return new RealtimeCronScheduleFactory(variableProcessor, packManager).createNewInstance(scheduleID, section);
    }
}
