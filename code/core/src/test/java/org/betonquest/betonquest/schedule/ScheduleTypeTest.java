package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.identifier.factory.IdentifierRegistry;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.FictiveTime;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.id.action.ActionIdentifierFactory;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.betonquest.betonquest.lib.instruction.section.DefaultSectionInstruction;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.schedule.ActionScheduling.ScheduleType;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.Server;
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
 * Tests for {@link ActionScheduling.ScheduleType}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
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
    private ScheduleIdentifier scheduleID;

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

    private ArgumentParsers argumentParsers;

    @BeforeEach
    void prepareConfig(final BetonQuestLogger logger) throws QuestException {
        final MultiConfiguration mockConfig = mock(MultiConfiguration.class);
        final ConfigurationOptions configurationOptions = mock(ConfigurationOptions.class);
        lenient().when(configurationOptions.pathSeparator()).thenReturn('.');
        lenient().when(mockConfig.options()).thenReturn(configurationOptions);
        lenient().when(questPackage.getConfig()).thenReturn(mockConfig);
        lenient().when(mockConfig.getString("actions.bell_ring")).thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        lenient().when(mockConfig.isString("actions.bell_ring")).thenReturn(true);
        lenient().when(mockConfig.contains("actions.bell_ring")).thenReturn(true);
        lenient().when(mockConfig.getString("actions.notify_goodNight")).thenReturn("notify &6Good night, sleep well!");
        lenient().when(mockConfig.isString("actions.notify_goodNight")).thenReturn(true);
        lenient().when(mockConfig.contains("actions.notify_goodNight")).thenReturn(true);

        lenient().when(scheduleID.getPackage()).thenReturn(questPackage);

        final IdentifierRegistry identifierRegistry = new IdentifierTypeRegistry(logger);
        identifierRegistry.register(ActionIdentifier.class, new ActionIdentifierFactory(packManager));
        identifierRegistry.register(ItemIdentifier.class, new ItemIdentifierFactory(packManager));
        argumentParsers = new DefaultArgumentParsers((i, p) -> null, mock(TextParser.class), mock(Server.class), identifierRegistry);

        lenient().when(section.getString("time")).thenReturn("22:00");
        lenient().when(section.contains("time")).thenReturn(true);
        lenient().when(section.getString("actions")).thenReturn("bell_ring,notify_goodNight");
        lenient().when(section.contains("actions")).thenReturn(true);
        lenient().when(section.getString("catchup")).thenReturn("NONE");
        lenient().when(section.contains("catchup")).thenReturn(true);
    }

    @SuppressWarnings("unchecked")
    private <T extends Schedule> Scheduler<T, FictiveTime> mockScheduler() {
        return mock(Scheduler.class);
    }

    private SectionInstruction getMockedInstruction(final ConfigurationSection section, final BetonQuestLoggerFactory loggerFactory) {
        return new DefaultSectionInstruction(argumentParsers, placeholders, packManager, questPackage, section, loggerFactory);
    }

    @Test
    void testCreate(final BetonQuestLoggerFactory loggerFactory) {
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(new MockedScheduleFactory(), scheduler);
        assertDoesNotThrow(() -> type.newScheduleInstance(scheduleID, getMockedInstruction(section, loggerFactory)), "");
    }

    @Test
    void testCreateThrowingUnchecked(final BetonQuestLoggerFactory loggerFactory) {
        final Scheduler<ThrowingUncheckedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<ThrowingUncheckedSchedule, FictiveTime> type =
                new ScheduleType<>(new ThrowingUncheckedScheduleFactory(), scheduler);
        assertThrows(IllegalArgumentException.class, () -> type.newScheduleInstance(scheduleID, getMockedInstruction(section, loggerFactory)));
    }

    @Test
    void testCreateInvalidInstruction(final BetonQuestLoggerFactory loggerFactory) {
        when(section.getString("time")).thenReturn(null);
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(new MockedScheduleFactory(), scheduler);
        assertThrows(QuestException.class, () -> type.newScheduleInstance(scheduleID, getMockedInstruction(section, loggerFactory)));
    }

    @Test
    void testAddSchedule(final BetonQuestLoggerFactory loggerFactory) {
        final Scheduler<MockedSchedule, FictiveTime> scheduler = mockScheduler();
        final ScheduleType<MockedSchedule, FictiveTime> type = new ScheduleType<>(new MockedScheduleFactory(), scheduler);
        assertDoesNotThrow(() -> type.createAndScheduleNewInstance(scheduleID, getMockedInstruction(section, loggerFactory)));
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
         * @param actions    the actions to execute
         * @param catchup    the catchup strategy
         */
        public MockedSchedule(final ScheduleIdentifier scheduleID, final List<ActionIdentifier> actions, final CatchupStrategy catchup) {
            super(scheduleID, actions, catchup);
        }
    }

    /**
     * Class extending a schedule that throws an unchecked action in its constructor.
     */
    private static class ThrowingUncheckedSchedule extends Schedule {

        /**
         * Creates new instance of the schedule.
         *
         * @param scheduleID the schedule id
         * @param actions    the actions to execute
         * @param catchup    the catchup strategy
         */
        public ThrowingUncheckedSchedule(final ScheduleIdentifier scheduleID, final List<ActionIdentifier> actions, final CatchupStrategy catchup) {
            super(scheduleID, actions, catchup);
            throw new IllegalArgumentException("unchecked");
        }
    }

    /**
     * Factory to create a Mocked Schedule.
     */
    private final class MockedScheduleFactory extends BaseScheduleFactory<MockedSchedule> {

        private MockedScheduleFactory() {
            super();
        }

        @Override
        public MockedSchedule createNewInstance(final ScheduleIdentifier scheduleID, final SectionInstruction instruction) throws QuestException {
            final ScheduleData scheduleData = parseScheduleData(instruction);
            return new MockedSchedule(scheduleID, scheduleData.actions(), scheduleData.catchup());
        }
    }

    /**
     * Factory to create a Throwing Unchecked Schedule.
     */
    private final class ThrowingUncheckedScheduleFactory extends BaseScheduleFactory<ThrowingUncheckedSchedule> {

        private ThrowingUncheckedScheduleFactory() {
            super();
        }

        @Override
        public ThrowingUncheckedSchedule createNewInstance(final ScheduleIdentifier scheduleID, final SectionInstruction instruction) throws QuestException {
            final ScheduleData scheduleData = parseScheduleData(instruction);
            return new ThrowingUncheckedSchedule(scheduleID, scheduleData.actions(), scheduleData.catchup());
        }
    }
}
