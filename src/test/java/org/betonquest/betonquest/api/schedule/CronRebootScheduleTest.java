package org.betonquest.betonquest.api.schedule;

import com.cronutils.model.Cron;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.betonquest.betonquest.api.schedule.CronSchedule.REBOOT_CRON_DEFINITION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link CronSchedule} that support reboot and custom nicknames.
 */
@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
public class CronRebootScheduleTest extends CronScheduleBaseTest {

    @Override
    protected CronSchedule createSchedule() throws QuestException {
        return new BaseScheduleFactory<>(variableProcessor, packManager) {
            @Override
            public CronSchedule createNewInstance(final ScheduleID scheduleID, final ConfigurationSection config) throws QuestException {
                final ScheduleData scheduleData = parseScheduleData(scheduleID.getPackage(), config);
                return new CronSchedule(scheduleID, scheduleData.events(), scheduleData.catchup(), REBOOT_CRON_DEFINITION, scheduleData.time()) {
                };
            }
        }.createNewInstance(scheduleID, section);
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
