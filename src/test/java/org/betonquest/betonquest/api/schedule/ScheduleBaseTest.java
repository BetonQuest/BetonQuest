package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * These tests should ensure that the basic parsing of schedules works properly.
 */
@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
@ExtendWith(BetonQuestLoggerService.class)
public class ScheduleBaseTest extends AbstractScheduleTest {

    @Override
    protected Schedule createSchedule() throws InstructionParseException {
        return new Schedule(scheduleID, section) {
        };
    }

    @Override
    protected void prepareConfig() {
        lenient().when(questPackage.getString("events.bell_ring")).thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        lenient().when(questPackage.getString("events.notify_goodNight")).thenReturn("notify &6Good night, sleep well!");

        lenient().when(section.getString("time")).thenReturn("22:00");
        lenient().when(section.getString("events")).thenReturn("bell_ring,notify_goodNight");
        lenient().when(section.getString("catchup")).thenReturn("NONE");
    }

    /**
     * Test once if a schedule with the provided sample configuration loads all values successfully.
     *
     * @throws InstructionParseException if parsing the schedule failed, test should fail
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testScheduleValidLoad() throws InstructionParseException {
        final Schedule schedule = createSchedule();
        assertEquals(scheduleID, schedule.getId(), "Schedule should return the id it was constructed with");
        assertEquals("22:00", schedule.getTime(), "Returned time should be correct");
        assertEquals(CatchupStrategy.NONE, schedule.getCatchup(), "Returned catchup strategy should be correct");
        assertEquals("bell_ring", schedule.getEvents().get(0).getBaseID(), "Returned events should contain 1st event");
        assertEquals("notify_goodNight", schedule.getEvents().get(1).getBaseID(), "Returned events should contain 2nd event");
    }

    @Test
    void testTimeNotSet() {
        when(section.getString("time")).thenReturn(null);
        final InstructionParseException exception = assertThrows(InstructionParseException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid time");
        assertEquals("Missing time instruction", exception.getMessage(), "InstructionParseException should have correct reason message");
    }

    @Test
    void testEventsNotSet() {
        when(section.getString("events")).thenReturn(null);
        final InstructionParseException exception = assertThrows(InstructionParseException.class, this::createSchedule, "Schedule should throw instruction parse exception for missing events");
        assertEquals("Missing events", exception.getMessage(), "InstructionParseException should have correct reason message");
    }

    @Test
    void testEventsNotFound() {
        when(questPackage.getString("events.bell_ring")).thenReturn(null);
        final InstructionParseException exception = assertThrows(InstructionParseException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid event names");
        assertInstanceOf(ObjectNotFoundException.class, exception.getCause(), "Cause should be ObjectNotFoundException");
    }

    @Test
    void testInvalidCatchup() {
        when(section.getString("catchup")).thenReturn("NotExistingCatchupStrategy");
        final InstructionParseException exception = assertThrows(InstructionParseException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid catchup");
        assertEquals("There is no such catchup strategy: NotExistingCatchupStrategy", exception.getMessage(), "InstructionParseException should have correct reason message");
    }

    @Test
    void testNoCatchup() throws InstructionParseException {
        when(section.getString("catchup")).thenReturn(null);
        final Schedule schedule = createSchedule();
        assertEquals(CatchupStrategy.NONE, schedule.getCatchup(), "Returned catchup strategy should be correct");
    }

    @Test
    void testLowerCaseCatchup() throws InstructionParseException {
        when(section.getString("catchup")).thenReturn("one");
        final Schedule schedule = createSchedule();
        assertEquals(CatchupStrategy.ONE, schedule.getCatchup(), "Returned catchup strategy should be correct");
    }
}
