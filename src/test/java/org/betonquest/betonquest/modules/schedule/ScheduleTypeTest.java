package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.schedule.FictiveTime;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.schedule.EventScheduling.ScheduleType;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link EventScheduling.ScheduleType}.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
class ScheduleTypeTest {

    /**
     * ID of the schedule to test.
     */
    @Mock
    private ScheduleID scheduleID;

    /**
     * Package of the schedule to test.
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

    @SuppressWarnings("unchecked")
    private <T extends Schedule> Scheduler<T, FictiveTime> mockScheduler() {
        return mock(Scheduler.class);
    }

    @Test
    void testCreate() {
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(MockedSchedule.class, scheduler);
        assertDoesNotThrow(() -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testCreateThrowingUnchecked() {
        final Scheduler<ThrowingUncheckedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<ThrowingUncheckedSchedule, FictiveTime> type = new ScheduleType<>(ThrowingUncheckedSchedule.class, scheduler);
        assertThrows(InvocationTargetException.class, () -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testCreateInvalidConstructor() {
        final Scheduler<InvalidConstructorSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<InvalidConstructorSchedule, FictiveTime> type = new ScheduleType<>(InvalidConstructorSchedule.class, scheduler);
        assertThrows(NoSuchMethodException.class, () -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testCreateInvalidInstruction() {
        when(section.getString("time")).thenReturn(null);
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(MockedSchedule.class, scheduler);
        assertThrows(InstructionParseException.class, () -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testAddSchedule() {
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(MockedSchedule.class, scheduler);
        assertDoesNotThrow(() -> type.createAndScheduleNewInstance(scheduleID, section), "");
        verify(scheduler).addSchedule(any());
    }

    /**
     * Class extending a schedule without any changes.
     */
    private static class MockedSchedule extends Schedule {

        /**
         * Creates new instance of the schedule.
         *
         * @param scheduleID  id of the new schedule
         * @param instruction config defining the schedule
         * @throws InstructionParseException if parsing the config failed
         */
        public MockedSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleID, instruction);
        }
    }

    /**
     * Class extending a schedule with a constructor that differs from the required signature.
     */
    private static class InvalidConstructorSchedule extends Schedule {

        /**
         * Creates new instance of the schedule.
         *
         * @param scheduleID  id of the new schedule
         * @param instruction config defining the schedule
         * @throws InstructionParseException if parsing the config failed
         */
        @SuppressWarnings("unused")
        public InvalidConstructorSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction, final String illegalArgument) throws InstructionParseException {
            super(scheduleID, instruction);
        }
    }

    /**
     * Class extending a schedule that throws an unchecked event in its constructor.
     */
    private static class ThrowingUncheckedSchedule extends Schedule {

        /**
         * Creates new instance of the schedule.
         *
         * @param scheduleID  id of the new schedule
         * @param instruction config defining the schedule
         * @throws InstructionParseException if parsing the config failed
         */
        public ThrowingUncheckedSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleID, instruction);
            throw new IllegalArgumentException("unchecked");
        }
    }
}
