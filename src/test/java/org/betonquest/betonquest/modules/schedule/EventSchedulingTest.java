package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.FictiveTime;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.schedule.EventScheduling.ScheduleType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests if starting and stopping EventScheduling works reliable and if loading Schedules works as intended.
 */
@ExtendWith(MockitoExtension.class)
class EventSchedulingTest {
    /**
     * Event Scheduling instance.
     */
    private EventScheduling scheduling;

    /**
     * Map holding all schedule types for {@link #scheduling}.
     */
    private Map<String, ScheduleType<?, ?>> scheduleTypes;

    @BeforeEach
    void setUp() {
        scheduleTypes = new HashMap<>();
        scheduling = new EventScheduling(mock(BetonQuestLogger.class), scheduleTypes);
    }

    @SuppressWarnings("unchecked")
    private Scheduler<?, FictiveTime> registerMockedType(final String name) {
        final Scheduler<MockedSchedule, FictiveTime> mockedScheduler = mock(Scheduler.class);
        scheduleTypes.put(name, new ScheduleType<>(MockedSchedule.class, mockedScheduler));
        return mockedScheduler;
    }

    @SuppressWarnings("unchecked")
    private ScheduleType<?, FictiveTime> registerSpyType(final String name) {
        final ScheduleType<Schedule, FictiveTime> spyType = spy(new ScheduleType<Schedule, FictiveTime>(Schedule.class, mock(Scheduler.class)));
        scheduleTypes.put(name, spyType);
        return spyType;
    }

    private QuestPackage mockQuestPackage(final String... contentFiles) throws KeyConflictException, InvalidSubConfigurationException {
        final List<? extends ConfigurationSection> configs = Arrays.stream(contentFiles)
                .map(File::new)
                .map(YamlConfiguration::loadConfiguration)
                .toList();
        final MultiConfiguration multiConfig = new MultiSectionConfiguration(configs);
        final QuestPackage pack = mock(QuestPackage.class);
        when(pack.getQuestPath()).thenReturn("test-modules-schedule");
        when(pack.getConfig()).thenReturn(multiConfig);
        return pack;
    }

    @Test
    void testStartAll() {
        final Scheduler<?, FictiveTime> schedulerA = registerMockedType("typeA");
        final Scheduler<?, FictiveTime> schedulerB = registerMockedType("typeB");
        scheduling.startAll();
        verify(schedulerA).start();
        verify(schedulerB).start();
    }

    @Test
    void testStartWithError() {
        final Scheduler<?, FictiveTime> throwingScheduler = registerMockedType("throwing");
        doThrow(RuntimeException.class).when(throwingScheduler).start();
        final List<? extends Scheduler<?, FictiveTime>> schedulers = IntStream.range(0, 10)
                .mapToObj(i -> "type" + (char) (i + 'A'))
                .map(this::registerMockedType)
                .toList();
        scheduling.startAll();
        verify(throwingScheduler).start();
        for (final Scheduler<?, FictiveTime> scheduler : schedulers) {
            verify(scheduler).start();
        }
    }

    @Test
    void testStopAll() {
        final Scheduler<?, FictiveTime> schedulerA = registerMockedType("typeA");
        final Scheduler<?, FictiveTime> schedulerB = registerMockedType("typeB");
        scheduling.stopAll();
        verify(schedulerA).stop();
        verify(schedulerB).stop();
    }

    @Test
    void testStopWithError() {
        final Scheduler<?, FictiveTime> throwingScheduler = registerMockedType("throwing");
        doThrow(RuntimeException.class).when(throwingScheduler).stop();
        final List<? extends Scheduler<?, FictiveTime>> schedulers = IntStream.range(0, 10)
                .mapToObj(i -> "type" + (char) (i + 'A'))
                .map(this::registerMockedType)
                .toList();
        scheduling.stopAll();
        verify(throwingScheduler).stop();
        for (final Scheduler<?, FictiveTime> scheduler : schedulers) {
            verify(scheduler).stop();
        }
    }

    @Test
    void testLoad() throws KeyConflictException, InvalidSubConfigurationException, InstructionParseException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        doNothing().when(simpleType).createAndScheduleNewInstance(any(), any());
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        doNothing().when(cronType).createAndScheduleNewInstance(any(), any());
        final QuestPackage pack = mockQuestPackage("src/test/resources/modules.schedule/packageExample.yml");
        scheduling.loadData(pack);
        verify(simpleType).createAndScheduleNewInstance(argThat(id -> "testSimple".equals(id.getBaseID())), any());
        verify(cronType).createAndScheduleNewInstance(argThat(id -> "testRealtime".equals(id.getBaseID())), any());
    }

    @Test
    void testLoadParseException() throws KeyConflictException, InvalidSubConfigurationException, InstructionParseException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/modules.schedule/packageExample.yml");
        doThrow(new InstructionParseException("error parsing schedule")).when(simpleType).createAndScheduleNewInstance(any(), any());
        scheduling.loadData(pack);
        verify(simpleType).createAndScheduleNewInstance(argThat(id -> "testSimple".equals(id.getBaseID())), any());
        verify(cronType).createAndScheduleNewInstance(argThat(id -> "testRealtime".equals(id.getBaseID())), any());
    }

    @Test
    void testLoadUncheckedException() throws KeyConflictException, InvalidSubConfigurationException, InstructionParseException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/modules.schedule/packageExample.yml");
        doThrow(new InvocationTargetException(new IllegalArgumentException())).when(simpleType).createAndScheduleNewInstance(any(), any());
        scheduling.loadData(pack);
        verify(simpleType).createAndScheduleNewInstance(argThat(id -> "testSimple".equals(id.getBaseID())), any());
        verify(cronType).createAndScheduleNewInstance(argThat(id -> "testRealtime".equals(id.getBaseID())), any());
    }

    @Test
    void testLoadCreationException() throws KeyConflictException, InvalidSubConfigurationException, InstructionParseException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/modules.schedule/packageExample.yml");
        doThrow(new NoSuchMethodException()).when(simpleType).createAndScheduleNewInstance(any(), any());
        scheduling.loadData(pack);
        verify(simpleType).createAndScheduleNewInstance(argThat(id -> "testSimple".equals(id.getBaseID())), any());
        verify(cronType).createAndScheduleNewInstance(argThat(id -> "testRealtime".equals(id.getBaseID())), any());
    }

    @Test
    void testLoadNoSchedules() throws KeyConflictException, InvalidSubConfigurationException, InstructionParseException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/modules.schedule/packageNoSchedules.yml");
        scheduling.loadData(pack);
        verify(simpleType, times(0)).createAndScheduleNewInstance(any(), any());
        verify(cronType, times(0)).createAndScheduleNewInstance(any(), any());
    }

    @Test
    void testLoadNameWithSpace() throws InstructionParseException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, KeyConflictException, InvalidSubConfigurationException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/modules.schedule/packageNameWithSpace.yml");
        scheduling.loadData(pack);
        verify(simpleType, times(0)).createAndScheduleNewInstance(any(), any());
        verify(cronType).createAndScheduleNewInstance(argThat(id -> "testRealtime".equals(id.getBaseID())), any());
    }

    /**
     * Class extending a schedule without any changes.
     */
    private static final class MockedSchedule extends Schedule {

        private MockedSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
            super(scheduleID, instruction);
        }
    }
}
