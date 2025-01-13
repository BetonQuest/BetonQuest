package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test Scheduler class.
 */
@ExtendWith(MockitoExtension.class)
class SchedulerTest {
    /**
     * Mocked Logger.
     */
    @Mock
    private BetonQuestLogger logger;

    /**
     * Mocked API.
     */
    @Mock
    private BetonQuestAPI questAPI;

    @Test
    void testAddSchedule() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questAPI);
        final ScheduleID scheduleID = mock(ScheduleID.class);
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleID);
        scheduler.addSchedule(schedule);
        assertTrue(scheduler.schedules.containsValue(schedule), "Schedules map should contain schedule");
        assertEquals(schedule, scheduler.schedules.get(scheduleID), "ScheduleID should be key of schedule");
    }

    @Test
    void testStart() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questAPI);
        assertFalse(scheduler.isRunning(), "isRunning should be false before start is called");
        scheduler.start();
        assertTrue(scheduler.isRunning(), "isRunning should be true after start is called");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testStop() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questAPI);
        final ScheduleID scheduleID = mock(ScheduleID.class);
        final Schedule schedule = mock(Schedule.class);
        scheduler.schedules.put(scheduleID, schedule);
        scheduler.start();
        assertTrue(scheduler.isRunning(), "isRunning should be true after start is called");
        scheduler.stop();
        assertFalse(scheduler.isRunning(), "isRunning should be false before stop is called");
        assertTrue(scheduler.schedules.isEmpty(), "Schedules map should be empty");
    }

    @Test
    void testExecuteEvents() {
        final BetonQuestAPI questAPI = mock(BetonQuestAPI.class);
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questAPI);
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(mock(ScheduleID.class));
        final EventID eventA = mock(EventID.class);
        final EventID eventB = mock(EventID.class);
        when(schedule.getEvents()).thenReturn(List.of(eventA, eventB));
        scheduler.executeEvents(schedule);
        verify(questAPI).event(null, eventA);
        verify(questAPI).event(null, eventB);
    }

    /**
     * Class extending a scheduler without any changes.
     */
    private static final class MockedScheduler extends Scheduler<Schedule, FictiveTime> {
        /**
         * Default constructor.
         *
         * @param logger   the logger that will be used for logging
         * @param questAPI the class for executing events
         */
        public MockedScheduler(final BetonQuestLogger logger, final BetonQuestAPI questAPI) {
            super(logger, questAPI);
        }

        @Override
        protected FictiveTime getNow() {
            return new FictiveTime();
        }
    }
}
