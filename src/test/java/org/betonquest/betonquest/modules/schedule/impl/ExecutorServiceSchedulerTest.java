package org.betonquest.betonquest.modules.schedule.impl;

import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import static org.betonquest.betonquest.modules.schedule.impl.ExecutorServiceScheduler.TERMINATION_TIMEOUT_MS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link ExecutorServiceScheduler}
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@ExtendWith(BetonQuestLoggerService.class)
class ExecutorServiceSchedulerTest {

    /**
     * The scheduler to test
     */
    private ExecutorServiceScheduler<Schedule> scheduler;

    /**
     * Executor mock used by the scheduler, will be set by {@link #newExecutor()}
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
        scheduler = spy(new ExecutorServiceScheduler<Schedule>(this::newExecutor) {
            @Override
            protected void schedule(final Schedule schedule) {
                //mock, do nothing
            }
        });
    }

    @Test
    void testStart() {
        final Schedule schedule1 = mockSchedule();
        final Schedule schedule2 = mockSchedule();
        scheduler.addSchedule(schedule1);
        scheduler.addSchedule(schedule2);
        scheduler.start();

        assertNotNull(scheduler.executor, "Executor should be present");
        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(scheduler).schedule(schedule1);
        verify(scheduler).schedule(schedule2);
    }

    @Test
    void testStopSuccess(final LogValidator validator) throws InterruptedException {
        scheduler.start();
        doReturn(true).when(executor).awaitTermination(anyLong(), any());
        scheduler.stop();

        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(executor).shutdownNow();
        verify(executor).awaitTermination(TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        validator.assertLogEntry(Level.FINE, "(Schedules) Stopping  scheduler.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Successfully shut down executor service.");
        validator.assertLogEntry(Level.FINE, "(Schedules) Stop complete.");
    }

    @Test
    void testStopInterrupted(final LogValidator validator) throws InterruptedException {
        scheduler.start();
        doThrow(InterruptedException.class).when(executor).awaitTermination(anyLong(), any());
        scheduler.stop();

        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(executor).shutdownNow();
        verify(executor).awaitTermination(TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        validator.assertLogEntry(Level.FINE, "(Schedules) Stopping  scheduler.");
        validator.assertLogEntry(Level.SEVERE, "(Schedules) Error while stopping scheduler", InterruptedException.class);
    }

    @Test
    void testStopTimeout(final LogValidator validator) throws InterruptedException {
        scheduler.start();
        doReturn(false).when(executor).awaitTermination(anyLong(), any());
        scheduler.stop();

        assertEquals(executor, scheduler.executor, "Executor should be provided by supplier");
        verify(executor).shutdownNow();
        verify(executor).awaitTermination(TERMINATION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        validator.assertLogEntry(Level.FINE, "(Schedules) Stopping  scheduler.");
        validator.assertLogEntry(
                Level.SEVERE,
                "(Schedules) Error while stopping scheduler",
                TimeoutException.class,
                "Not all schedules could be terminated within time constraints"
        );
    }
}
