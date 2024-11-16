package org.betonquest.betonquest.modules.schedule.impl.realtime.cron;

import com.cronutils.model.time.ExecutionTime;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Tests for the {@link RealtimeCronScheduler}
 */
@ExtendWith(MockitoExtension.class)
class RealtimeCronSchedulerTest {
    /**
     * Mocked schedule id.
     */
    private static final ScheduleID SCHEDULE_ID = mock(ScheduleID.class);

    static {
        when(SCHEDULE_ID.toString()).thenReturn("test.schedule");
    }

    /**
     * The current time used in the tests.
     */
    private final Instant now = Instant.now();

    @Mock
    private BetonQuestLogger logger;

    private static RealtimeCronSchedule getSchedule(final CatchupStrategy catchupStrategy, final boolean shouldRunOnReboot) {
        final RealtimeCronSchedule schedule = mock(RealtimeCronSchedule.class);
        when(schedule.shouldRunOnReboot()).thenReturn(shouldRunOnReboot);
        when(schedule.getId()).thenReturn(SCHEDULE_ID);
        when(schedule.getCatchup()).thenReturn(catchupStrategy);
        return schedule;
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithoutSchedules() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final RealtimeCronScheduler scheduler = spy(new RealtimeCronScheduler(logger, cache));
        scheduler.start(now);

        verify(logger, times(1)).debug("Starting realtime scheduler.");
        verify(logger, times(1)).debug("Collecting reboot schedules...");
        verify(logger, times(1)).debug("Found 0 reboot schedules. They will be run on next server tick.");
        verify(logger, times(1)).debug("Found 0 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Realtime scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(scheduler, never()).schedule(any(), any());
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithRebootSchedules() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final RealtimeCronScheduler scheduler = new RealtimeCronScheduler(logger, cache);
        final RealtimeCronSchedule schedule = getSchedule(CatchupStrategy.NONE, true);

        final ExecutionTime executionTime = mock(ExecutionTime.class);
        when(executionTime.timeToNextExecution(any())).thenReturn(Optional.empty());
        when(schedule.getExecutionTime()).thenReturn(executionTime);

        scheduler.addSchedule(schedule);
        scheduler.start(now);

        verify(logger, times(1)).debug("Starting realtime scheduler.");
        verify(logger, times(1)).debug("Collecting reboot schedules...");
        verify(logger, times(1)).debug("Found 1 reboot schedules. They will be run on next server tick.");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Found 0 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Realtime scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(1)).getEvents();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithMissedSchedulesStrategyOne() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = now.minusSeconds(60);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));

        final RealtimeCronScheduler scheduler = new RealtimeCronScheduler(logger, cache);
        final RealtimeCronSchedule schedule = getSchedule(CatchupStrategy.ONE, false);
        final ExecutionTime executionTime = mock(ExecutionTime.class);
        final ZonedDateTime nextMissedExecution = lastExecution.plusSeconds(30).atZone(ZoneId.systemDefault());
        when(executionTime.nextExecution(any())).thenReturn(Optional.of(nextMissedExecution));
        when(schedule.getExecutionTime()).thenReturn(executionTime);
        scheduler.addSchedule(schedule);
        scheduler.start(now);

        verify(logger, times(1)).debug("Starting realtime scheduler.");
        verify(logger, times(1)).debug("Collecting reboot schedules...");
        verify(logger, times(1)).debug("Found 0 reboot schedules. They will be run on next server tick.");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution);
        verify(logger, times(1)).debug("Found 1 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Running missed schedules to catch up...");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Realtime scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(1)).getEvents();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithMissedSchedulesStrategyAll() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = now.minusSeconds(65);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));

        final RealtimeCronScheduler scheduler = new RealtimeCronScheduler(logger, cache);
        final RealtimeCronSchedule schedule = getSchedule(CatchupStrategy.ALL, false);
        final ExecutionTime executionTime = mock(ExecutionTime.class);
        final ZonedDateTime nextMissedExecution1 = lastExecution.plusSeconds(20).atZone(ZoneId.systemDefault());
        final ZonedDateTime nextMissedExecution2 = lastExecution.plusSeconds(40).atZone(ZoneId.systemDefault());
        final ZonedDateTime nextMissedExecution3 = lastExecution.plusSeconds(60).atZone(ZoneId.systemDefault());
        final ZonedDateTime nextMissedExecution4 = lastExecution.plusSeconds(80).atZone(ZoneId.systemDefault());
        when(executionTime.nextExecution(any())).thenReturn(
                Optional.of(nextMissedExecution1),
                Optional.of(nextMissedExecution2),
                Optional.of(nextMissedExecution3),
                Optional.of(nextMissedExecution4));
        when(schedule.getExecutionTime()).thenReturn(executionTime);
        scheduler.addSchedule(schedule);
        scheduler.start(now);

        verify(logger, times(1)).debug("Starting realtime scheduler.");
        verify(logger, times(1)).debug("Collecting reboot schedules...");
        verify(logger, times(1)).debug("Found 0 reboot schedules. They will be run on next server tick.");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution1);
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution2);
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution3);
        verify(logger, times(1)).debug("Found 3 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Running missed schedules to catch up...");
        verify(logger, times(3)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Realtime scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(3)).getEvents();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithoutMissedSchedulesStrategyAll() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = now.minusSeconds(40);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));

        final RealtimeCronScheduler scheduler = new RealtimeCronScheduler(logger, cache);
        final RealtimeCronSchedule schedule = getSchedule(CatchupStrategy.ALL, false);
        final ExecutionTime executionTime = mock(ExecutionTime.class);
        final ZonedDateTime nextExecution = now.plusSeconds(20).atZone(ZoneId.systemDefault());
        when(executionTime.nextExecution(any())).thenReturn(Optional.of(nextExecution));
        when(schedule.getExecutionTime()).thenReturn(executionTime);
        scheduler.addSchedule(schedule);
        scheduler.start(now);

        verify(logger, times(1)).debug("Starting realtime scheduler.");
        verify(logger, times(1)).debug("Collecting reboot schedules...");
        verify(logger, times(1)).debug("Found 0 reboot schedules. They will be run on next server tick.");
        verify(logger, times(1)).debug("Found 0 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Realtime scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, never()).getEvents();
    }

    @Test
    @SuppressWarnings({"PMD.DoNotUseThreads", "PMD.JUnitTestContainsTooManyAsserts"})
    void testStartSchedule() {
        final Duration duration = Duration.ofSeconds(20);
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        @SuppressWarnings("PMD.CloseResource") final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        when(executorService.schedule(any(Runnable.class), eq(duration.toMillis()), eq(TimeUnit.MILLISECONDS))).then(invocation -> {
            final Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        });
        final RealtimeCronScheduler scheduler = new RealtimeCronScheduler(logger, () -> executorService, cache);
        final RealtimeCronSchedule schedule = getSchedule(CatchupStrategy.NONE, false);
        final ExecutionTime executionTime = mock(ExecutionTime.class);
        when(executionTime.timeToNextExecution(any())).thenReturn(
                Optional.of(duration),
                Optional.of(duration),
                Optional.of(duration),
                Optional.empty());
        when(schedule.getExecutionTime()).thenReturn(executionTime);
        scheduler.addSchedule(schedule);
        scheduler.start(now);

        verify(logger, times(1)).debug("Starting realtime scheduler.");
        verify(logger, times(1)).debug("Collecting reboot schedules...");
        verify(logger, times(1)).debug("Found 0 reboot schedules. They will be run on next server tick.");
        verify(logger, times(1)).debug("Found 0 missed schedule runs that will be caught up.");
        verify(logger, times(3)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Realtime scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(3)).getEvents();
    }
}
