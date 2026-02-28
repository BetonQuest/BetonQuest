package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.action.ActionManager;
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
    private ActionManager actionManager;

    @Test
    void testAddSchedule() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, actionManager);
        final ScheduleIdentifier scheduleID = mock(ScheduleIdentifier.class);
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleID);
        scheduler.addSchedule(schedule);
        assertTrue(scheduler.schedules.containsValue(schedule), "Schedules map should contain schedule");
        assertEquals(schedule, scheduler.schedules.get(scheduleID), "ScheduleID should be key of schedule");
    }

    @Test
    void testStart() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, actionManager);
        assertFalse(scheduler.isRunning(), "isRunning should be false before start is called");
        scheduler.start();
        assertTrue(scheduler.isRunning(), "isRunning should be true after start is called");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testStop() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, actionManager);
        final ScheduleIdentifier scheduleID = mock(ScheduleIdentifier.class);
        final Schedule schedule = mock(Schedule.class);
        scheduler.schedules.put(scheduleID, schedule);
        scheduler.start();
        assertTrue(scheduler.isRunning(), "isRunning should be true after start is called");
        scheduler.stop();
        assertFalse(scheduler.isRunning(), "isRunning should be false before stop is called");
        assertTrue(scheduler.schedules.isEmpty(), "Schedules map should be empty");
    }

    @Test
    void testExecuteActions() {
        final ActionManager actionManager = mock(ActionManager.class);
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, actionManager);
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(mock(ScheduleIdentifier.class));
        final ActionIdentifier actionA = mock(ActionIdentifier.class);
        final ActionIdentifier actionB = mock(ActionIdentifier.class);
        final List<ActionIdentifier> actionList = List.of(actionA, actionB);
        when(schedule.getActions()).thenReturn(actionList);
        scheduler.executeActions(schedule);
        verify(actionManager).run(null, actionList);
    }

    /**
     * Class extending a scheduler without any changes.
     */
    private static final class MockedScheduler extends Scheduler<Schedule, FictiveTime> {

        /**
         * Default constructor.
         *
         * @param logger        the logger that will be used for logging
         * @param actionManager the action manager
         */
        private MockedScheduler(final BetonQuestLogger logger, final ActionManager actionManager) {
            super(logger, actionManager);
        }

        @Override
        protected FictiveTime getNow() {
            return new FictiveTime();
        }
    }
}
