package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.schedule.EventScheduling.ScheduleType;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link EventScheduling.ScheduleType}
 */
@ExtendWith(BetonQuestLoggerService.class)
class ScheduleTypeTest {

    /**
     * ID of the schedule to test.
     */
    private ScheduleID scheduleID;

    /**
     * Configuration section of the schedule to test
     */
    private ConfigurationSection section;

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

    @SuppressWarnings("unchecked")
    private <T extends Schedule> Scheduler<T> mockScheduler() {
        return mock(Scheduler.class);
    }

    @Test
    void testCreate() {
        final Scheduler<MockedSchedule> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule> type = new ScheduleType<>(MockedSchedule.class, scheduler);
        assertDoesNotThrow(() -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testCreateThrowingUnchecked() {
        final Scheduler<ThrowingUncheckedSchedule> scheduler = mockScheduler();
        final ScheduleType<ThrowingUncheckedSchedule> type = new ScheduleType<>(ThrowingUncheckedSchedule.class, scheduler);
        assertThrows(InvocationTargetException.class, () -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testCreateInvalidConstructor() {
        final Scheduler<InvalidConstructorSchedule> scheduler = mockScheduler();
        final ScheduleType<InvalidConstructorSchedule> type = new ScheduleType<>(InvalidConstructorSchedule.class, scheduler);
        assertThrows(NoSuchMethodException.class, () -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testCreateInvalidInstruction() {
        when(section.getString("time")).thenReturn(null);
        final Scheduler<MockedSchedule> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule> type = new ScheduleType<>(MockedSchedule.class, scheduler);
        assertThrows(InstructionParseException.class, () -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testAddSchedule() {
        final Scheduler<MockedSchedule> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule> type = new ScheduleType<>(MockedSchedule.class, scheduler);
        assertDoesNotThrow(() -> type.createAndScheduleNewInstance(scheduleID, section), "");
        verify(scheduler).addSchedule(any());
    }

    /**
     * Class extending a schedule without any changes
     */
    private static class MockedSchedule extends Schedule {

        public MockedSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleID, instruction);
        }
    }

    /**
     * Class extending a schedule with a constructor that differs from the required signature
     */
    private static class InvalidConstructorSchedule extends Schedule {

        @SuppressWarnings("unused")
        public InvalidConstructorSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction, final String illegalArgument) throws InstructionParseException {
            super(scheduleID, instruction);
        }
    }

    /**
     * Class extending a schedule that throws an unchecked event in its constructor
     */
    private static class ThrowingUncheckedSchedule extends Schedule {

        public ThrowingUncheckedSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleID, instruction);
            throw new IllegalArgumentException("unchecked");
        }
    }
}
