package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.schedule.EventScheduling.ScheduleType;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests if starting & stopping EventScheduling works reliable and if loading Schedules works as intended.
 */
@ExtendWith(BetonQuestLoggerService.class)
class EventSchedulingTest {

    /**
     * Event Scheduling instance
     */
    private EventScheduling scheduling;

    /**
     * Map holding all schedule types for {@link #scheduling}.
     */
    private Map<String, ScheduleType<?>> scheduleTypes;

    /**
     * Default constructor.
     */
    public EventSchedulingTest() {
    }

    @BeforeEach
    void setUp() {
        scheduleTypes = new HashMap<>();
        scheduling = new EventScheduling(scheduleTypes);
    }

    @SuppressWarnings("unchecked")
    private Scheduler<?> registerMockedType(final String name) {
        final Scheduler<MockedSchedule> mockedScheduler = mock(Scheduler.class);
        scheduleTypes.put(name, new ScheduleType<>(MockedSchedule.class, mockedScheduler));
        return mockedScheduler;
    }

    @Test
    void testStartAll() {
        final Scheduler<?> schedulerA = registerMockedType("typeA");
        final Scheduler<?> schedulerB = registerMockedType("typeB");
        scheduling.startAll();
        verify(schedulerA).start();
        verify(schedulerB).start();
    }

    @Test
    void testStartWithError() {
        final Scheduler<?> throwingScheduler = registerMockedType("throwing");
        doThrow(RuntimeException.class).when(throwingScheduler).start();
        final List<? extends Scheduler<?>> schedulers = IntStream.range(0, 10)
                .mapToObj(i -> "type" + (char) (i + 'A'))
                .map(this::registerMockedType)
                .toList();
        scheduling.startAll();
        verify(throwingScheduler).start();
        for (final Scheduler<?> scheduler : schedulers) {
            verify(scheduler).start();
        }
    }

    @Test
    void testStopAll() {
        final Scheduler<?> schedulerA = registerMockedType("typeA");
        final Scheduler<?> schedulerB = registerMockedType("typeB");
        scheduling.stopAll();
        verify(schedulerA).stop();
        verify(schedulerB).stop();
    }

    @Test
    void testStopWithError() {
        final Scheduler<?> throwingScheduler = registerMockedType("throwing");
        doThrow(RuntimeException.class).when(throwingScheduler).start();
        final List<? extends Scheduler<?>> schedulers = IntStream.range(0, 10)
                .mapToObj(i -> "type" + (char) (i + 'A'))
                .map(this::registerMockedType)
                .toList();
        scheduling.stopAll();
        verify(throwingScheduler).stop();
        for (final Scheduler<?> scheduler : schedulers) {
            verify(scheduler).stop();
        }
    }

    @Test
    @Disabled
    void testLoad() {
        //TODO
        fail("todo");
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
