package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
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
    private QuestTypeApi questTypeApi;

    @Test
    void testAddSchedule() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questTypeApi);
        final ScheduleID scheduleID = mock(ScheduleID.class);
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleID);
        scheduler.addSchedule(schedule);
        assertTrue(scheduler.schedules.containsValue(schedule), "Schedules map should contain schedule");
        assertEquals(schedule, scheduler.schedules.get(scheduleID), "ScheduleID should be key of schedule");
    }

    @Test
    void testStart() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questTypeApi);
        assertFalse(scheduler.isRunning(), "isRunning should be false before start is called");
        scheduler.start();
        assertTrue(scheduler.isRunning(), "isRunning should be true after start is called");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testStop() {
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questTypeApi);
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
        final QuestTypeApi questTypeApi = mock(QuestTypeApi.class);
        final Scheduler<Schedule, FictiveTime> scheduler = new MockedScheduler(logger, questTypeApi);
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(mock(ScheduleID.class));
        final EventID eventA = mock(EventID.class);
        final EventID eventB = mock(EventID.class);
        final List<EventID> eventList = List.of(eventA, eventB);
        when(schedule.getEvents()).thenReturn(eventList);
        scheduler.executeEvents(schedule);
        verify(questTypeApi).events(null, eventList);
    }

    /**
     * Class extending a scheduler without any changes.
     */
    private static final class MockedScheduler extends Scheduler<Schedule, FictiveTime> {

        /**
         * Default constructor.
         *
         * @param logger       the logger that will be used for logging
         * @param questTypeApi the class for executing actions
         */
        public MockedScheduler(final BetonQuestLogger logger, final QuestTypeApi questTypeApi) {
            super(logger, questTypeApi);
        }

        @Override
        protected FictiveTime getNow() {
            return new FictiveTime();
        }
    }
}
