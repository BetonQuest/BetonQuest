package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.schedule.FictiveTime;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.betonquest.betonquest.schedule.EventScheduling.ScheduleType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
     * Registry holding all schedule types for {@link #scheduling}.
     */
    private ScheduleRegistry scheduleTypes;

    @BeforeEach
    void setUp() {
        scheduleTypes = new ScheduleRegistry(mock(BetonQuestLogger.class));
        scheduling = new EventScheduling(mock(BetonQuestLogger.class), mock(QuestPackageManager.class), scheduleTypes);
    }

    @SuppressWarnings("unchecked")
    private Scheduler<?, FictiveTime> registerMockedType(final String name) {
        final Scheduler<MockedSchedule, FictiveTime> mockedScheduler = mock(Scheduler.class);
        scheduleTypes.register(name, new ScheduleType<>(MockedSchedule::new, mockedScheduler));
        return mockedScheduler;
    }

    @SuppressWarnings("unchecked")
    private ScheduleType<?, FictiveTime> registerSpyType(final String name) {
        final ScheduleType<Schedule, FictiveTime> spyType = spy(new ScheduleType<Schedule, FictiveTime>(MockedSchedule::new, mock(Scheduler.class)));
        scheduleTypes.register(name, spyType);
        return spyType;
    }

    private QuestPackage mockQuestPackage(final String... contentFiles) throws KeyConflictException, InvalidSubConfigurationException {
        final List<? extends ConfigurationSection> configs = Arrays.stream(contentFiles)
                .map(File::new)
                .map(YamlConfiguration::loadConfiguration)
                .toList();
        final MultiConfiguration multiConfig = new MultiSectionConfiguration(configs);
        final QuestPackage pack = mock(QuestPackage.class);
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
        scheduling.clear();
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
        scheduling.clear();
        verify(throwingScheduler).stop();
        for (final Scheduler<?, FictiveTime> scheduler : schedulers) {
            verify(scheduler).stop();
        }
    }

    @Test
    void testLoad() throws KeyConflictException, InvalidSubConfigurationException, QuestException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        doNothing().when(simpleType).createAndScheduleNewInstance(any(), any(), any());
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        doNothing().when(cronType).createAndScheduleNewInstance(any(), any(), any());
        final QuestPackage pack = mockQuestPackage("src/test/resources/schedule/packageExample.yml");
        scheduling.load(pack);
        verify(simpleType).createAndScheduleNewInstance(any(), argThat(id -> "testSimple".equals(id.get())), any());
        verify(cronType).createAndScheduleNewInstance(any(), argThat(id -> "testRealtime".equals(id.get())), any());
    }

    @Test
    void testLoadParseException() throws KeyConflictException, InvalidSubConfigurationException, QuestException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/schedule/packageExample.yml");
        doThrow(new QuestException("error parsing schedule")).when(simpleType).createAndScheduleNewInstance(any(), any(), any());
        scheduling.load(pack);
        verify(simpleType).createAndScheduleNewInstance(any(), argThat(id -> "testSimple".equals(id.get())), any());
        verify(cronType).createAndScheduleNewInstance(any(), argThat(id -> "testRealtime".equals(id.get())), any());
    }

    @Test
    void testLoadUncheckedException() throws KeyConflictException, InvalidSubConfigurationException, QuestException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/schedule/packageExample.yml");
        doThrow(new QuestException(new IllegalArgumentException())).when(simpleType).createAndScheduleNewInstance(any(), any(), any());
        scheduling.load(pack);
        verify(simpleType).createAndScheduleNewInstance(any(), argThat(id -> "testSimple".equals(id.get())), any());
        verify(cronType).createAndScheduleNewInstance(any(), argThat(id -> "testRealtime".equals(id.get())), any());
    }

    @Test
    void testLoadNoSchedules() throws KeyConflictException, InvalidSubConfigurationException, QuestException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/schedule/packageNoSchedules.yml");
        scheduling.load(pack);
        verify(simpleType, times(0)).createAndScheduleNewInstance(any(), any(), any());
        verify(cronType, times(0)).createAndScheduleNewInstance(any(), any(), any());
    }

    @Test
    void testLoadNameWithSpace() throws QuestException, KeyConflictException, InvalidSubConfigurationException {
        final ScheduleType<?, FictiveTime> simpleType = registerSpyType("realtime-daily");
        final ScheduleType<?, FictiveTime> cronType = registerSpyType("realtime-cron");
        final QuestPackage pack = mockQuestPackage("src/test/resources/schedule/packageNameWithSpace.yml");
        scheduling.load(pack);
        verify(simpleType, times(0)).createAndScheduleNewInstance(any(), any(), any());
        verify(cronType).createAndScheduleNewInstance(any(), argThat(id -> "testRealtime".equals(id.get())), any());
    }

    /**
     * Class extending a schedule without any changes.
     */
    private static final class MockedSchedule extends Schedule {

        private MockedSchedule(final QuestPackageManager packManager, final ScheduleID scheduleID,
                               final ConfigurationSection instruction) throws QuestException {
            super(packManager, scheduleID, instruction);
        }
    }
}
