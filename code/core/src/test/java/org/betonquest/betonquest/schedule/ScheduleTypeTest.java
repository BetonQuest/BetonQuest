package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.FictiveTime;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.schedule.EventScheduling.ScheduleType;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link EventScheduling.ScheduleType}.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
class ScheduleTypeTest {

    /**
     * {@link Placeholders} to create and resolve placeholders.
     */
    @Mock
    private Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    @Mock
    private QuestPackageManager packManager;

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
        final MultiConfiguration mockConfig = mock(MultiConfiguration.class);
        final ConfigurationOptions configurationOptions = mock(ConfigurationOptions.class);
        lenient().when(configurationOptions.pathSeparator()).thenReturn('.');
        lenient().when(mockConfig.options()).thenReturn(configurationOptions);
        lenient().when(questPackage.getConfig()).thenReturn(mockConfig);
        lenient().when(mockConfig.getString("events.bell_ring"))
                .thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        lenient().when(mockConfig.getString("events.notify_goodNight"))
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
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(new MockedScheduleFactory(), scheduler);
        assertDoesNotThrow(() -> type.newScheduleInstance(scheduleID, section), "");
    }

    @Test
    void testCreateThrowingUnchecked() {
        final Scheduler<ThrowingUncheckedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<ThrowingUncheckedSchedule, FictiveTime> type =
                new ScheduleType<>(new ThrowingUncheckedScheduleFactory(), scheduler);
        assertThrows(IllegalArgumentException.class, () -> type.newScheduleInstance(scheduleID, section));
    }

    @Test
    void testCreateInvalidInstruction() {
        when(section.getString("time")).thenReturn(null);
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(new MockedScheduleFactory(), scheduler);
        assertThrows(QuestException.class, () -> type.newScheduleInstance(scheduleID, section));
    }

    @Test
    void testAddSchedule() {
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(new MockedScheduleFactory(), scheduler);
        assertDoesNotThrow(() -> type.createAndScheduleNewInstance(scheduleID, section));
        verify(scheduler).addSchedule(any());
    }

    /**
     * Class extending a schedule without any changes.
     */
    private static class MockedSchedule extends Schedule {

        /**
         * Creates new instance of the schedule.
         *
         * @param scheduleID the schedule id
         * @param events     the events to execute
         * @param catchup    the catchup strategy
         */
        public MockedSchedule(final ScheduleID scheduleID, final List<EventID> events, final CatchupStrategy catchup) {
            super(scheduleID, events, catchup);
        }
    }

    /**
     * Class extending a schedule that throws an unchecked event in its constructor.
     */
    private static class ThrowingUncheckedSchedule extends Schedule {

        /**
         * Creates new instance of the schedule.
         *
         * @param scheduleID the schedule id
         * @param events     the events to execute
         * @param catchup    the catchup strategy
         */
        public ThrowingUncheckedSchedule(final ScheduleID scheduleID, final List<EventID> events, final CatchupStrategy catchup) {
            super(scheduleID, events, catchup);
            throw new IllegalArgumentException("unchecked");
        }
    }

    /**
     * Factory to create a Mocked Schedule.
     */
    private final class MockedScheduleFactory extends BaseScheduleFactory<MockedSchedule> {

        private MockedScheduleFactory() {
            super(placeholders, packManager);
        }

        @Override
        public MockedSchedule createNewInstance(final ScheduleID scheduleID, final ConfigurationSection config) throws QuestException {
            final ScheduleData scheduleData = parseScheduleData(scheduleID.getPackage(), config);
            return new MockedSchedule(scheduleID, scheduleData.events(), scheduleData.catchup());
        }
    }

    /**
     * Factory to create a Throwing Unchecked Schedule.
     */
    private final class ThrowingUncheckedScheduleFactory extends BaseScheduleFactory<ThrowingUncheckedSchedule> {

        private ThrowingUncheckedScheduleFactory() {
            super(placeholders, packManager);
        }

        @Override
        public ThrowingUncheckedSchedule createNewInstance(final ScheduleID scheduleID, final ConfigurationSection config) throws QuestException {
            final ScheduleData scheduleData = parseScheduleData(scheduleID.getPackage(), config);
            return new ThrowingUncheckedSchedule(scheduleID, scheduleData.events(), scheduleData.catchup());
        }
    }
}
