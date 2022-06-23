package org.betonquest.betonquest.api.schedule;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.cronutils.model.field.expression.FieldExpression.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static org.betonquest.betonquest.api.schedule.CronSchedule.DEFAULT_CRON_DEFINITION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * These tests should ensure that the parsing of cron schedules works properly.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(BetonQuestLoggerService.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyStaticImports"})
class CronScheduleTest {

    /**
     * ID of the schedule to test.
     */
    @Mock
    private ScheduleID scheduleID;

    /**
     * Quest package of the schedule to test.
     */
    @Mock
    private QuestPackage questPackage;


    /**
     * Configuration section of the schedule to test.
     */
    @Mock
    private ConfigurationSection section;

    @BeforeEach
    void prepareConfig() {
        when(questPackage.getString("events.bell_ring")).thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        when(questPackage.getString("events.notify_goodNight")).thenReturn("notify &6Good night, sleep well!");

        when(scheduleID.getPackage()).thenReturn(questPackage);

        when(section.getString("time")).thenReturn("0 22 * * *");
        when(section.getString("events")).thenReturn("bell_ring,notify_goodNight");
        when(section.getString("catchup")).thenReturn(null);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    void testScheduleValidLoad() throws InstructionParseException {
        final CronSchedule schedule = new MockedSchedule(scheduleID, section);
        assertEquals("0 22 * * *", schedule.getTime(), "Returned time should be correct");

        final Cron cron = schedule.getTimeCron();
        assertNotNull(cron, "time cron should not be null");
        assertDoesNotThrow(cron::validate, "Cron should be valid");
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(22)).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(cron), "Cron expression should be as expected");
        assertFalse(schedule.shouldRunOnReboot(), "Schedule should not run on reboot");

        assertNotEquals(Optional.empty(), schedule.getNextExecution(), "Schedule should provide next execution time");
        assertNotEquals(Optional.empty(), schedule.getLastExecution(), "Schedule should provide last execution time");
    }

    @Test
    void testInvalidTime() {
        when(section.getString("time")).thenReturn("22:00");
        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> new MockedSchedule(scheduleID, section), "Schedule should throw instruction parse exception for invalid cron syntax");
        assertEquals("Time is no valid cron syntax: '22:00'", exception.getMessage(), "InstructionParseException should have correct reason message");
    }

    @Test
    void testDifrentCronDefinition() {
        when(section.getString("time")).thenReturn("0 22 * * * *");
        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> new MockedSchedule(scheduleID, section), "Schedule should throw instruction parse exception for invalid cron syntax");
        assertEquals("Time is no valid cron syntax: '0 22 * * * *'", exception.getMessage(), "InstructionParseException should have correct reason message");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    void testRebootCron() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@reboot");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);

        assertTrue(schedule.shouldRunOnReboot(), "Schedules onReboot flag should be true");
        final Cron cron = schedule.getTimeCron();
        assertNotNull(cron, "time cron should not be null");
        assertDoesNotThrow(cron::validate, "Cron should be valid");
        assertEquals(Optional.empty(), schedule.getNextExecution(), "Schedule should not provide a next execution time");
        assertEquals(Optional.empty(), schedule.getLastExecution(), "Schedule should not provide a last execution time");
    }

    @Test
    void testCronHourly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@hourly");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(always()).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@hourly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronDaily() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@daily");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@daily cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronMidnight() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@midnight");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@midnight cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronWeekly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@weekly");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(always()).withMonth(always()).withDoW(on(0)).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@weekly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronMonthly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@monthly");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(on(1)).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@monthly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronYearly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@yearly");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(on(1)).withMonth(on(1)).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@yearly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronAnnually() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@annually");
        final CronSchedule schedule = new MockedRebootSchedule(scheduleID, section);
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(on(1)).withMonth(on(1)).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@annually cron is not equal to equivalent as defined on wiki");
    }


    /**
     * Class extending a cron schedule without any changes.
     */
    private static class MockedSchedule extends CronSchedule {
        private MockedSchedule(final ScheduleID scheduleId, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleId, instruction);
        }
    }

    /**
     * Class extending a cron schedule with {@code @reboot} expression support.
     */
    private static class MockedRebootSchedule extends CronSchedule {
        private MockedRebootSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleID, instruction, DEFAULT_CRON_DEFINITION, true);
        }
    }
}
