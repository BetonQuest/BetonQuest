package org.betonquest.betonquest.api.schedule;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static com.cronutils.model.field.expression.FieldExpression.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static org.betonquest.betonquest.api.schedule.CronSchedule.DEFAULT_CRON_DEFINITION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * These tests should ensure that the parsing of cron schedules works properly.
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.JUnit5TestShouldBePackagePrivate"})
public class CronScheduleBaseTest extends ScheduleBaseTest {

    @Override
    protected CronSchedule createSchedule() throws InstructionParseException {
        return new CronSchedule(scheduleID, section) {
        };
    }

    @Override
    protected void prepareConfig() {
        super.prepareConfig();
        lenient().when(section.getString("time")).thenReturn("0 22 * * *");
        lenient().when(section.getString("catchup")).thenReturn(null);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testScheduleValidLoad() throws InstructionParseException {
        final CronSchedule schedule = createSchedule();
        assertEquals("0 22 * * *", schedule.getTime(), "Returned time should be correct");

        final Cron cron = schedule.getTimeCron();
        assertNotNull(cron, "time cron should not be null");
        assertDoesNotThrow(cron::validate, "Cron should be valid");
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(22)).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(cron), "Cron expression should be as expected");
        assertFalse(schedule.shouldRunOnReboot(), "Schedule should not run on reboot");

        final ZonedDateTime now = ZonedDateTime.now();
        assertNotEquals(Optional.empty(), schedule.getExecutionTime().nextExecution(now), "Schedule should provide next execution time");
        assertNotEquals(Optional.empty(), schedule.getExecutionTime().lastExecution(now), "Schedule should provide last execution time");
    }

    @Test
    void testInvalidTime() {
        when(section.getString("time")).thenReturn("22:00");
        final InstructionParseException exception = assertThrows(InstructionParseException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid cron syntax");
        assertEquals("Time is no valid cron syntax: '22:00'", exception.getMessage(), "InstructionParseException should have correct reason message");
    }

    @Test
    void testDifferentCronDefinition() {
        when(section.getString("time")).thenReturn("0 22 * * * *");
        final InstructionParseException exception = assertThrows(InstructionParseException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid cron syntax");
        assertEquals("Time is no valid cron syntax: '0 22 * * * *'", exception.getMessage(), "InstructionParseException should have correct reason message");
    }

    @Test
    void testCronHourly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@hourly");
        final CronSchedule schedule = createSchedule();
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(always()).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@hourly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronDaily() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@daily");
        final CronSchedule schedule = createSchedule();
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@daily cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronMidnight() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@midnight");
        final CronSchedule schedule = createSchedule();
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(always()).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@midnight cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronWeekly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@weekly");
        final CronSchedule schedule = createSchedule();
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(always()).withMonth(always()).withDoW(on(0)).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@weekly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronMonthly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@monthly");
        final CronSchedule schedule = createSchedule();
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(on(1)).withMonth(always()).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@monthly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronYearly() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@yearly");
        final CronSchedule schedule = createSchedule();
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(on(1)).withMonth(on(1)).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@yearly cron is not equal to equivalent as defined on wiki");
    }

    @Test
    void testCronAnnually() throws InstructionParseException {
        when(section.getString("time")).thenReturn("@annually");
        final CronSchedule schedule = createSchedule();
        final Cron expected = CronBuilder.cron(DEFAULT_CRON_DEFINITION).withMinute(on(0)).withHour(on(0)).withDoM(on(1)).withMonth(on(1)).withDoW(always()).instance();
        assertTrue(expected.equivalent(schedule.getTimeCron()), "@annually cron is not equal to equivalent as defined on wiki");
    }
}
