package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test Scheduler class.
 */
@ExtendWith(BetonQuestLoggerService.class)
class SchedulerTest {
    private Schedule mockSchedule(final ScheduleID scheduleID) {
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleID);
        return schedule;
    }

    @Test
    void testAddSchedule(final BetonQuestLogger logger) {
        final Scheduler<Schedule> scheduler = new MockedScheduler(logger);
        final ScheduleID scheduleID = mock(ScheduleID.class);
        final Schedule schedule = mockSchedule(scheduleID);
        scheduler.addSchedule(schedule);
        assertTrue(scheduler.schedules.containsValue(schedule), "Schedules map should contain schedule");
        assertEquals(schedule, scheduler.schedules.get(scheduleID), "ScheduleID should be key of schedule");
    }

    @Test
    void testStart(final BetonQuestLogger logger) {
        final Scheduler<Schedule> scheduler = new MockedScheduler(logger);
        assertFalse(scheduler.isRunning(), "isRunning should be false before start is called");
        scheduler.start();
        assertTrue(scheduler.isRunning(), "isRunning should be true after start is called");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    void testStop(final BetonQuestLogger logger) {
        final Scheduler<Schedule> scheduler = new MockedScheduler(logger);
        final ScheduleID scheduleID = mock(ScheduleID.class);
        final Schedule schedule = mockSchedule(scheduleID);
        scheduler.schedules.put(scheduleID, schedule);
        scheduler.start();
        assertTrue(scheduler.isRunning(), "isRunning should be true after start is called");
        scheduler.stop();
        assertFalse(scheduler.isRunning(), "isRunning should be false before stop is called");
        assertTrue(scheduler.schedules.isEmpty(), "Schedules map should be empty");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testExecuteEvents(final MockedStatic<BetonQuest> betonQuest, final BetonQuestLogger logger) {
        final Scheduler<Schedule> scheduler = new MockedScheduler(logger);
        final Schedule schedule = mockSchedule(mock(ScheduleID.class));
        final EventID eventA = mock(EventID.class);
        final EventID eventB = mock(EventID.class);
        when(schedule.getEvents()).thenReturn(List.of(eventA, eventB));
        scheduler.executeEvents(schedule);
        betonQuest.verify(() -> BetonQuest.event(null, eventA));
        betonQuest.verify(() -> BetonQuest.event(null, eventB));
    }

    /**
     * Class extending a scheduler without any changes.
     */
    private static class MockedScheduler extends Scheduler<Schedule> {
        /**
         * Default constructor.
         *
         * @param log the logger that will be used for logging
         */
        public MockedScheduler(final BetonQuestLogger logger) {
            super(logger);
        }
    }
}
