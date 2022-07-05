package org.betonquest.betonquest.modules.schedule.impl.simple;

import org.betonquest.betonquest.api.schedule.CronRebootScheduleTest;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.schedule.impl.realtime.RealtimeSchedule;

/**
 * Tests for realtime schedule.
 */
public class RealtimeScheduleTest extends CronRebootScheduleTest {

    @Override
    protected RealtimeSchedule createSchedule() throws InstructionParseException {
        return new RealtimeSchedule(scheduleID, section);
    }
}
