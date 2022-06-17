package org.betonquest.betonquest.modules.schedule.impl.simple;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the simple schedule
 */
@ExtendWith(BetonQuestLoggerService.class)
@SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts", "PMD.AvoidDuplicateLiterals", "PMD.TooManyStaticImports"})
class SimpleScheduleTest {

    /**
     * The DateTimeFormatter used for parsing the time strings.
     */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * ID of the schedule to test.
     */
    private ScheduleID scheduleID;

    /**
     * Configuration section of the schedule to test
     */
    private ConfigurationSection section;

    /**
     * Default constructor.
     */
    public SimpleScheduleTest() {
    }

    @BeforeEach
    void prepareConfig() {
        final QuestPackage questPackage = mock(QuestPackage.class);
        when(questPackage.getString("events.bell_ring"))
                .thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        when(questPackage.getString("events.notify_goodNight"))
                .thenReturn("notify &6Good night, sleep well!");

        scheduleID = mock(ScheduleID.class);
        when(scheduleID.getPackage()).thenReturn(questPackage);

        section = mock(ConfigurationSection.class);
        when(section.getString("time")).thenReturn("22:00");
        when(section.getString("events")).thenReturn("bell_ring,notify_goodNight");
        when(section.getString("catchup")).thenReturn("NONE");
    }


    @Test
    void testScheduleValidLoad() throws InstructionParseException {
        final SimpleSchedule schedule = new SimpleSchedule(scheduleID, section);
        assertEquals(LocalTime.of(22, 0), schedule.getTimeToRun(), "Returned time should be correct");
    }


    @Test
    void testScheduleInvalidLoad() {
        when(section.getString("time")).thenReturn("0 22 * * * *");
        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> new SimpleSchedule(scheduleID, section), "Schedule should throw instruction parse exception for invalid time format");
        final String expected = "Unable to parse time '0 22 * * * *': ";
        assertTrue(exception.getMessage().startsWith(expected), "InstructionParseException should have correct reason message");
    }

    @Test
    void testScheduleNextExecutionIn1H() throws InstructionParseException {
        final LocalDateTime targetTime = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0);
        when(section.getString("time")).thenReturn(targetTime.format(TIME_FORMAT));
        final SimpleSchedule schedule = new SimpleSchedule(scheduleID, section);
        assertEquals(targetTime.toLocalTime(), schedule.getTimeToRun(), "Returned time should be correct");
        final Instant expected = targetTime.toInstant(OffsetDateTime.now().getOffset());
        assertEquals(expected, schedule.getNextExecution(), "Next execution time should be as expected");
    }

    @Test
    void testScheduleNextExecutionIn23H() throws InstructionParseException {
        final LocalDateTime targetTime = LocalDateTime.now().plusHours(23).withSecond(0).withNano(0);
        when(section.getString("time")).thenReturn(targetTime.format(TIME_FORMAT));
        final SimpleSchedule schedule = new SimpleSchedule(scheduleID, section);
        assertEquals(targetTime.toLocalTime(), schedule.getTimeToRun(), "Returned time should be correct");
        final Instant expected = targetTime.toInstant(OffsetDateTime.now().getOffset());
        assertEquals(expected, schedule.getNextExecution(), "Next execution time should be as expected");
    }
}
