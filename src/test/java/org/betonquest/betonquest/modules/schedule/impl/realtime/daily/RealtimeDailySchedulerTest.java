package org.betonquest.betonquest.modules.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link RealtimeDailyScheduler}
 */
@ExtendWith(BetonQuestLoggerService.class)
@Disabled
@SuppressWarnings("PMD.DoNotUseThreads")
class RealtimeDailySchedulerTest {

    /**
     * Mocked schedule id.
     */
    private static final ScheduleID SCHEDULE_ID = mock(ScheduleID.class);

    static {
        when(SCHEDULE_ID.toString()).thenReturn("test.schedule");
    }

    @NotNull
    private static RealtimeDailySchedule getSchedule(final CatchupStrategy catchupStrategy) {
        final RealtimeDailySchedule schedule = mock(RealtimeDailySchedule.class);

        when(schedule.getId()).thenReturn(SCHEDULE_ID);
        when(schedule.getCatchup()).thenReturn(catchupStrategy);

        return schedule;
    }

    @Test
    void testStartWithoutSchedules(final LogValidator validator) {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final RealtimeDailyScheduler scheduler = spy(new RealtimeDailyScheduler(cache));
        scheduler.start();

        validator.assertLogEntry(Level.FINE, "(Schedules) Starting simple scheduler.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Collecting missed schedules...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Found 0 missed schedule runs that will be caught up.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Simple scheduler start complete.");
        validator.assertEmpty();
        verify(scheduler, never()).schedule(any());
    }

    @Test
    void testStartWithMissedSchedulesStrategyOne(final LogValidator validator) {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = Instant.now().minus(2, ChronoUnit.DAYS).plusSeconds(60);
        final Instant nextMissedExecution = lastExecution.plus(1, ChronoUnit.DAYS);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));
        final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(() -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.ONE);
        when(schedule.getNextExecution()).thenReturn(Instant.now());
        scheduler.addSchedule(schedule);
        scheduler.start();

        validator.assertLogEntry(Level.FINE, "(Schedules) Starting simple scheduler.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Collecting missed schedules...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' run missed at " + nextMissedExecution);
        validator.assertLogEntry(Level.FINE, "(Schedules) Found 1 missed schedule runs that will be caught up.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Running missed schedules to catch up...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' runs its events...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Simple scheduler start complete.");
        validator.assertEmpty();
        verify(schedule, times(1)).getEvents();
    }

    @Test
    void testStartWithMissedSchedulesStrategyAll(final LogValidator validator) {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(60);
        final Instant nextMissedExecution1 = lastExecution.plus(1, ChronoUnit.DAYS);
        final Instant nextMissedExecution2 = lastExecution.plus(2, ChronoUnit.DAYS);
        final Instant nextMissedExecution3 = lastExecution.plus(3, ChronoUnit.DAYS);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));
        final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(() -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.ALL);
        when(schedule.getNextExecution()).thenReturn(Instant.now());
        scheduler.addSchedule(schedule);
        scheduler.start();

        validator.assertLogEntry(Level.FINE, "(Schedules) Starting simple scheduler.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Collecting missed schedules...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' run missed at " + nextMissedExecution1);
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' run missed at " + nextMissedExecution2);
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' run missed at " + nextMissedExecution3);
        validator.assertLogEntry(Level.FINE, "(Schedules) Found 3 missed schedule runs that will be caught up.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Running missed schedules to catch up...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' runs its events...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' runs its events...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' runs its events...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Simple scheduler start complete.");
        validator.assertEmpty();
        verify(schedule, times(3)).getEvents();
    }

    @Test
    void testStartSchedule(final LogValidator validator) {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant nextExecution1 = Instant.now();
        final Instant nextExecution2 = nextExecution1.plus(1, ChronoUnit.DAYS);
        final Instant nextExecution3 = nextExecution1.plus(2, ChronoUnit.DAYS);
        final Instant nextExecution4 = nextExecution1.plus(3, ChronoUnit.DAYS);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(nextExecution1));
        final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        when(executorService.schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS))).then(invocation -> {
            final Runnable runnable = invocation.getArgument(0);
            final long delay = invocation.getArgument(1);
            if (!Instant.now().plusMillis(delay + 1000).isAfter(nextExecution4)) {
                runnable.run();
            }
            return null;
        });
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(() -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.NONE);
        when(schedule.getNextExecution()).thenReturn(nextExecution1, nextExecution2, nextExecution3, nextExecution4);
        scheduler.addSchedule(schedule);
        scheduler.start();

        validator.assertLogEntry(Level.FINE, "(Schedules) Starting simple scheduler.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Collecting missed schedules...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Found 0 missed schedule runs that will be caught up.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' runs its events...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' runs its events...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Schedule 'test.schedule' runs its events...");
        validator.assertLogEntry(Level.FINE, "(Schedules) Simple scheduler start complete.");
        validator.assertEmpty();
        verify(schedule, times(3)).getEvents();
    }
}
