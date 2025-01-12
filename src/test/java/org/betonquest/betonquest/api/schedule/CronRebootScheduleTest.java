package org.betonquest.betonquest.api.schedule;

import com.cronutils.model.Cron;
import org.betonquest.betonquest.exceptions.QuestException;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.betonquest.betonquest.api.schedule.CronSchedule.REBOOT_CRON_DEFINITION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link CronSchedule} that support reboot and custom nicknames
 */
@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
public class CronRebootScheduleTest extends CronScheduleBaseTest {

    @Override
    protected CronSchedule createSchedule() throws QuestException {
        return new CronSchedule(scheduleID, section, REBOOT_CRON_DEFINITION) {
        };
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testRebootScheduleValidLoad() throws QuestException {
        when(section.getString("time")).thenReturn("@reboot");
        final CronSchedule schedule = createSchedule();

        assertTrue(schedule.shouldRunOnReboot(), "Schedules onReboot flag should be true");
        final Cron cron = schedule.getTimeCron();
        assertNotNull(cron, "time cron should not be null");
        assertDoesNotThrow(cron::validate, "Cron should be valid");
        final ZonedDateTime now = ZonedDateTime.now();
        assertEquals(Optional.empty(), schedule.getExecutionTime().nextExecution(now), "Schedule should not provide a next execution time");
        assertEquals(Optional.empty(), schedule.getExecutionTime().lastExecution(now), "Schedule should not provide a last execution time");
    }
}
