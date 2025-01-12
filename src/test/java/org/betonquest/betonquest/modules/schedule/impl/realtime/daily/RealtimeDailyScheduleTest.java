package org.betonquest.betonquest.modules.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.schedule.ScheduleBaseTest;
import org.betonquest.betonquest.exceptions.QuestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the daily realtime schedule.
 */
@ExtendWith(MockitoExtension.class)
class RealtimeDailyScheduleTest extends ScheduleBaseTest {

    /**
     * The DateTimeFormatter used for parsing the time strings.
     */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    protected RealtimeDailySchedule createSchedule() throws QuestException {
        return new RealtimeDailySchedule(scheduleID, section);
    }

    @Override
    protected void prepareConfig() {
        super.prepareConfig();
        lenient().when(section.getString("catchup")).thenReturn("NONE");
        lenient().when(section.getString("time")).thenReturn("22:00");
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
    public void testScheduleValidLoad() throws QuestException {
        final RealtimeDailySchedule schedule = createSchedule();
        assertEquals(LocalTime.of(22, 0), schedule.getTimeToRun(), "Returned time should be correct");
    }

    @Test
    void testScheduleInvalidLoad() {
        when(section.getString("time")).thenReturn("0 22 * * * *");
        final QuestException exception = assertThrows(QuestException.class, () -> new RealtimeDailySchedule(scheduleID, section), "Schedule should throw instruction parse exception for invalid time format");
        final String expected = "Unable to parse time '0 22 * * * *': ";
        assertTrue(exception.getMessage().startsWith(expected), "QuestException should have correct reason message");
    }

    @Test
    void testScheduleNextExecutionIn1H() throws QuestException {
        final LocalDateTime targetTime = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0);
        when(section.getString("time")).thenReturn(targetTime.format(TIME_FORMAT));
        final RealtimeDailySchedule schedule = new RealtimeDailySchedule(scheduleID, section);
        assertEquals(targetTime.toLocalTime(), schedule.getTimeToRun(), "Returned time should be correct");
        final OffsetDateTime now = OffsetDateTime.now();
        final Instant expected = targetTime.toInstant(now.getOffset());
        assertEquals(expected, schedule.getNextExecution(now.toInstant()), "Next execution time should be as expected");
    }

    @Test
    void testScheduleNextExecutionIn23H() throws QuestException {
        final LocalDateTime targetTime = LocalDateTime.now().plusHours(23).withSecond(0).withNano(0);
        when(section.getString("time")).thenReturn(targetTime.format(TIME_FORMAT));
        final RealtimeDailySchedule schedule = new RealtimeDailySchedule(scheduleID, section);
        assertEquals(targetTime.toLocalTime(), schedule.getTimeToRun(), "Returned time should be correct");
        final OffsetDateTime now = OffsetDateTime.now();
        final Instant expected = targetTime.toInstant(now.getOffset());
        assertEquals(expected, schedule.getNextExecution(now.toInstant()), "Next execution time should be as expected");
    }
}
