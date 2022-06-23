package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * These tests should ensure that the basic parsing of schedules works properly.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(BetonQuestLoggerService.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
class ScheduleTest {

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
        lenient().when(questPackage.getString("events.bell_ring"))
                .thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        lenient().when(questPackage.getString("events.notify_goodNight"))
                .thenReturn("notify &6Good night, sleep well!");

        lenient().when(scheduleID.getPackage()).thenReturn(questPackage);

        lenient().when(section.getString("time")).thenReturn("22:00");
        lenient().when(section.getString("events")).thenReturn("bell_ring,notify_goodNight");
        lenient().when(section.getString("catchup")).thenReturn("NONE");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    void testScheduleValidLoad() throws InstructionParseException {
        final Schedule schedule = new MockedSchedule(scheduleID, section);
        assertEquals(scheduleID, schedule.getId(), "Schedule should return the id it was constructed with");
        assertEquals("22:00", schedule.getTime(), "Returned time should be correct");
        assertEquals(CatchupStrategy.NONE, schedule.getCatchup(), "Returned catchup strategy should be correct");
        assertEquals("bell_ring", schedule.getEvents().get(0).getBaseID(),
                "Returned events should contain 1st event");
        assertEquals("notify_goodNight", schedule.getEvents().get(1).getBaseID(),
                "Returned events should contain 2nd event");
    }

    @Test
    void testTimeNotSet() {
        when(section.getString("time")).thenReturn(null);
        final InstructionParseException exception = assertThrows(
                InstructionParseException.class,
                () -> new MockedSchedule(scheduleID, section),
                "Schedule should throw instruction parse exception for invalid time"
        );
        assertEquals("Missing time instruction", exception.getMessage(),
                "InstructionParseException should have correct reason message");
    }

    @Test
    void testEventsNotSet() {
        when(section.getString("events")).thenReturn(null);
        final InstructionParseException exception = assertThrows(
                InstructionParseException.class,
                () -> new MockedSchedule(scheduleID, section),
                "Schedule should throw instruction parse exception for missing events"
        );
        assertEquals("Missing events", exception.getMessage(),
                "InstructionParseException should have correct reason message");
    }

    @Test
    void testEventsNotFound() {
        when(questPackage.getString("events.bell_ring")).thenReturn(null);
        final InstructionParseException exception = assertThrows(
                InstructionParseException.class,
                () -> new MockedSchedule(scheduleID, section),
                "Schedule should throw instruction parse exception for invalid event names"
        );
        assertInstanceOf(ObjectNotFoundException.class, exception.getCause(),
                "Cause should be ObjectNotFoundException");
    }

    @Test
    void testInvalidCatchup() {
        when(section.getString("catchup")).thenReturn("fvztwes");
        final InstructionParseException exception = assertThrows(
                InstructionParseException.class,
                () -> new MockedSchedule(scheduleID, section),
                "Schedule should throw instruction parse exception for invalid catchup"
        );
        assertEquals("There is no such catchup strategy: fvztwes", exception.getMessage(),
                "InstructionParseException should have correct reason message");
    }

    @Test
    void testNoCatchup() throws InstructionParseException {
        when(section.getString("catchup")).thenReturn(null);
        final Schedule schedule = new MockedSchedule(scheduleID, section);
        assertEquals(CatchupStrategy.NONE, schedule.getCatchup(),
                "Returned catchup strategy should be correct");
    }

    @Test
    void testLowerCaseCatchup() throws InstructionParseException {
        when(section.getString("catchup")).thenReturn("one");
        final Schedule schedule = new MockedSchedule(scheduleID, section);
        assertEquals(CatchupStrategy.ONE, schedule.getCatchup(),
                "Returned catchup strategy should be correct");
    }

    /**
     * Class extending a schedule without any changes
     */
    private static class MockedSchedule extends Schedule {
        private MockedSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleID, instruction);
        }
    }
}
