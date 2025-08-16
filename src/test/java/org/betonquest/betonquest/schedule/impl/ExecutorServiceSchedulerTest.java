package org.betonquest.betonquest.schedule.impl;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.schedule.FictiveTime;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.betonquest.betonquest.schedule.impl.ExecutorServiceScheduler.TERMINATION_TIMEOUT_MS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link ExecutorServiceScheduler}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@ExtendWith(MockitoExtension.class)
class ExecutorServiceSchedulerTest {
    /**
     * Mocked logger.
     */
    @Mock
    private BetonQuestLogger logger;

    /**
     * The scheduler to test.
     */
    private ExecutorServiceScheduler<Schedule, FictiveTime> scheduler;

    /**
     * Executor mock used by the scheduler, will be set by {@link #newExecutor()}.
     */
    private ScheduledExecutorService executor;

    private ScheduledExecutorService newExecutor() {
        executor = mock(ScheduledExecutorService.class);
        return executor;
    }

    private Schedule mockSchedule() {
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(mock(ScheduleID.class));
        return schedule;
    }

    @BeforeEach
    void setUp() {
        scheduler = spy(new ExecutorServiceScheduler<>(logger, mock(QuestTypeApi.class), this::newExecutor) {

            @Override
            protected FictiveTime getNow() {
                return new FictiveTime();
            }

            @Override
            protected void schedule(final FictiveTime now, final Schedule schedule) {
                // do nothing
            }
        });
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testStart() {
        final Schedule schedule1 = mockSchedule();
        final Schedule schedule2 = mockSchedule();
        scheduler.addSchedule(schedule1);
        scheduler.addSchedule(schedule2);
        scheduler.start();

        assertNotNull(scheduler.executor, "Executor should be present");
        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(scheduler, times(1)).schedule(any(), eq(schedule1));
        verify(scheduler, times(1)).schedule(any(), eq(schedule2));
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testStopSuccess() throws InterruptedException {
        scheduler.start();
        doReturn(true).when(executor).awaitTermination(anyLong(), any());
        scheduler.stop();

        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(executor).shutdownNow();
        verify(executor).awaitTermination(TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        verify(logger, times(1)).debug("Stopping  scheduler.");
        verify(logger, times(1)).debug("Successfully shut down executor service.");
        verify(logger, times(1)).debug("Stop complete.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testStopInterrupted() throws InterruptedException {
        scheduler.start();
        doThrow(InterruptedException.class).when(executor).awaitTermination(anyLong(), any());
        scheduler.stop();

        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(executor).shutdownNow();
        verify(executor).awaitTermination(TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        verify(logger, times(1)).debug("Stopping  scheduler.");
        verify(logger, times(1)).error(eq("Error while stopping scheduler"), any(InterruptedException.class));
        verify(logger, times(1)).debug("Stop complete.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testStopTimeout() throws InterruptedException {
        scheduler.start();
        doReturn(false).when(executor).awaitTermination(anyLong(), any());
        scheduler.stop();

        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(executor).shutdownNow();
        verify(executor).awaitTermination(TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        verify(logger, times(1)).debug("Stopping  scheduler.");
        verify(logger, times(1)).error(eq("Error while stopping scheduler"), any(TimeoutException.class));
        verify(logger, times(1)).debug("Stop complete.");
        verifyNoMoreInteractions(logger);
    }
}
