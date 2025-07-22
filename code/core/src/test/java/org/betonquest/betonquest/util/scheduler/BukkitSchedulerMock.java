package org.betonquest.betonquest.util.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.junit.jupiter.api.Assertions;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A mock for {@link BukkitScheduler}.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.DoNotUseThreads", "PMD.GodClass", "PMD.CyclomaticComplexity",
        "PMD.ExcessivePublicCount"})
public class BukkitSchedulerMock implements BukkitScheduler, AutoCloseable, Closeable {
    /**
     * Exception message for deprecated methods.
     */
    private static final String MESSAGE_DEPRECATED = "Not Implemented because it is already deprecated!";

    /**
     * Exception message for rarely used methods.
     */
    private static final String MESSAGE_UNUSED = "Not Implemented because it is rarely used!";

    /**
     * Exception message for methods that have a better replacement method.
     */
    private static final String MESSAGE_UNWANTED = "Not Implemented because it should not be used! Instead use ";

    /**
     * The thread pool for the execution of all threads.
     */
    private final ThreadPoolExecutor pool;

    /**
     * List of all scheduled tasks.
     */
    private final TaskList scheduledTasks;

    /**
     * Deque containing all exceptions that occurred during async execution in chronological order.
     */
    private final Deque<ExecutionException> asyncExceptions;

    /**
     * List of all {@link Future} {@link BukkitTask}s that have been executed async with the
     * {@link ThreadPoolExecutor#execute(Runnable)} method.
     */
    private final List<Future<?>> asyncTasks;

    /**
     * The current tick that should be executed.
     */
    private long currentTick;

    /**
     * The task id for the next task.
     */
    private int taskId;

    /**
     * Creates a new {@link BukkitSchedulerMock}.
     */
    public BukkitSchedulerMock() {
        pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        scheduledTasks = new TaskList();
        asyncExceptions = new LinkedList<>();
        asyncTasks = new ArrayList<>();
    }

    private static Runnable wrapTask(final ScheduledTask task) {
        return () -> {
            task.setRunning(true);
            task.run();
            task.setRunning(false);
        };
    }

    /**
     * Gets the current tick of the server.
     *
     * @return The current tick of the server.
     */
    public long getCurrentTick() {
        return currentTick;
    }

    /**
     * Performs one tick on the server.
     */
    public void performTick() {
        currentTick++;
        final List<ScheduledTask> oldTasks = scheduledTasks.getCurrentTaskList();

        for (final ScheduledTask task : oldTasks) {
            if (task.getScheduledTick() == currentTick && !task.isCancelled()) {
                try {
                    executeTask(task);
                } catch (final InterruptedException interruptedException) {
                    if (Thread.interrupted()) {
                        return;
                    }
                }

                rescheduleRepeatingTasks(task);
            }
        }
    }

    private void rescheduleRepeatingTasks(final ScheduledTask task) {
        if (task instanceof RepeatingTask && !task.isCancelled()) {
            ((RepeatingTask) task).updateScheduledTick();
            scheduledTasks.addTask(task);
        }
    }

    private void executeTask(final ScheduledTask task) throws InterruptedException {
        if (task.isSync()) {
            wrapTask(task).run();
        } else {
            try {
                pool.submit(wrapTask(task)).get();
            } catch (final ExecutionException e) {
                asyncExceptions.addLast(e);
            }
        }
    }

    /**
     * Performs a number of ticks on the server.
     *
     * @param ticks The number of ticks to execute.
     */
    public void performTicks(final long ticks) {
        for (long i = 0; i < ticks; i++) {
            performTick();
        }
    }

    /**
     * Waits until all asynchronous tasks have finished executing or the timeout elapses.
     * The default timeout is one second.
     * <p>
     * Keep in mind that the timeout still takes effect while debugging tests!
     * Waiting too long at a break point can affect the execution order of your test
     * compared to the normal execution!
     *
     * @return true when all async tasks have finished
     */
    public boolean waitAsyncTasksFinished() {
        return waitAsyncTasksFinished(1000L);
    }

    /**
     * Waits until all asynchronous tasks have finished executing or the timeout elapses.
     * <p>
     * Keep in mind that the timeout still takes effect while debugging tests!
     * Waiting too long at a break point can affect the execution order of your test
     * compared to the normal execution!
     *
     * @param timeout the timeout in milliseconds
     * @return true when all async tasks have finished
     */
    public boolean waitAsyncTasksFinished(final long timeout) {
        final long untilTimeMillis = System.currentTimeMillis() + timeout;
        while (!asyncTasks.isEmpty() && System.currentTimeMillis() < untilTimeMillis) {
            asyncTasks.removeIf(Future::isDone);
            try {
                Thread.sleep(10L);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return asyncTasks.isEmpty();
    }

    @Override
    public BukkitTask runTask(final Plugin plugin, final Runnable task) {
        return runTaskLater(plugin, task, 1L);
    }

    @Override
    public BukkitTask runTask(final Plugin plugin, final BukkitRunnable task) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public BukkitTask runTaskLater(final Plugin plugin, final Runnable task, final long delay) {
        final ScheduledTask scheduledTask = new ScheduledTask(taskId++, plugin, true, currentTick + Math.max(delay, 1), task);
        scheduledTasks.addTask(scheduledTask);
        return scheduledTask;
    }

    @Override
    public BukkitTask runTaskTimer(final Plugin plugin, final Runnable task, final long delay, final long period) {
        final RepeatingTask repeatingTask = new RepeatingTask(taskId++, plugin, true, currentTick + Math.max(delay, 1), period, task);
        scheduledTasks.addTask(repeatingTask);
        return repeatingTask;
    }

    @Override
    public BukkitTask runTaskTimer(final Plugin plugin, final BukkitRunnable task, final long delay, final long period) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public int scheduleSyncDelayedTask(final Plugin plugin, final Runnable task, final long delay) {
        throw new UnsupportedOperationException(MESSAGE_UNWANTED + "runTaskLater");
    }

    @Override
    public int scheduleSyncDelayedTask(final Plugin plugin, final BukkitRunnable task, final long delay) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public int scheduleSyncDelayedTask(final Plugin plugin, final Runnable task) {
        throw new UnsupportedOperationException(MESSAGE_UNWANTED + "runTask");
    }

    @Override
    public int scheduleSyncDelayedTask(final Plugin plugin, final BukkitRunnable task) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public int scheduleSyncRepeatingTask(final Plugin plugin, final Runnable task, final long delay, final long period) {
        throw new UnsupportedOperationException(MESSAGE_UNWANTED + "runTaskTimer");
    }

    @Override
    public int scheduleSyncRepeatingTask(final Plugin plugin, final BukkitRunnable task, final long delay, final long period) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public int scheduleAsyncDelayedTask(final Plugin plugin, final Runnable task, final long delay) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public int scheduleAsyncDelayedTask(final Plugin plugin, final Runnable task) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public int scheduleAsyncRepeatingTask(final Plugin plugin, final Runnable task, final long delay, final long period) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public <T> Future<T> callSyncMethod(final Plugin plugin, final Callable<T> task) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void cancelTask(final int taskId) {
        scheduledTasks.cancelTask(taskId);
    }

    @Override
    public void cancelTasks(final Plugin plugin) {
        for (final ScheduledTask task : scheduledTasks.getCurrentTaskList()) {
            if (plugin.equals(task.getOwner())) {
                task.cancel();
            }
        }
    }

    @Override
    public boolean isCurrentlyRunning(final int taskId) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public boolean isQueued(final int taskId) {
        for (final ScheduledTask task : scheduledTasks.getCurrentTaskList()) {
            if (task.getTaskId() == taskId) {
                return !task.isCancelled();
            }
        }
        return false;
    }

    @Override
    public List<BukkitWorker> getActiveWorkers() {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public List<BukkitTask> getPendingTasks() {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public BukkitTask runTaskAsynchronously(final Plugin plugin, final Runnable task) {
        final ScheduledTask scheduledTask = new ScheduledTask(taskId++, plugin, false, currentTick, task);
        asyncTasks.add(pool.submit(wrapTask(scheduledTask)));
        return scheduledTask;
    }

    @Override
    public BukkitTask runTaskAsynchronously(final Plugin plugin, final BukkitRunnable task) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public BukkitTask runTaskLater(final Plugin plugin, final BukkitRunnable task, final long delay) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(final Plugin plugin, final Runnable task, final long delay) {
        final ScheduledTask scheduledTask = new ScheduledTask(taskId++, plugin, false, currentTick + delay, task);
        scheduledTasks.addTask(scheduledTask);
        return scheduledTask;
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(final Plugin plugin, final BukkitRunnable task, final long delay) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(final Plugin plugin, final Runnable task, final long delay, final long period) {
        final RepeatingTask scheduledTask = new RepeatingTask(taskId++, plugin, false, currentTick + delay, period, task);
        scheduledTasks.addTask(scheduledTask);
        return scheduledTask;
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(final Plugin plugin, final BukkitRunnable task, final long delay, final long period) {
        throw new UnsupportedOperationException(MESSAGE_DEPRECATED);
    }

    @Override
    public Executor getMainThreadExecutor(final Plugin plugin) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void runTask(final Plugin plugin, final Consumer<BukkitTask> task) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void runTaskAsynchronously(final Plugin plugin, final Consumer<BukkitTask> task) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void runTaskLater(final Plugin plugin, final Consumer<BukkitTask> task, final long delay) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void runTaskLaterAsynchronously(final Plugin plugin, final Consumer<BukkitTask> task, final long delay) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void runTaskTimer(final Plugin plugin, final Consumer<BukkitTask> task, final long delay, final long period) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void runTaskTimerAsynchronously(final Plugin plugin, final Consumer<BukkitTask> task, final long delay, final long period) {
        throw new UnsupportedOperationException(MESSAGE_UNUSED);
    }

    @Override
    public void close() {
        pool.shutdown();
    }

    /**
     * Get the next async execution exception that happened during ticking. The exceptions are returned in chronological
     * order but with no guarantees about the task they came from.
     *
     * @return the last execution
     * @throws NoSuchElementException if there are no exceptions remaining
     * @see #hasAsyncExceptionRemaining()
     */
    public ExecutionException nextAsyncException() {
        return asyncExceptions.removeFirst();
    }

    /**
     * Check whether any async execution exceptions are remaining.
     *
     * @return true if there are exceptions; false otherwise
     */
    public boolean hasAsyncExceptionRemaining() {
        return !asyncExceptions.isEmpty();
    }

    /**
     * Assert that no exceptions have been thrown during async execution.
     */
    public void assertNoExceptions() {
        Assertions.assertFalse(hasAsyncExceptionRemaining(), "No exceptions should have been thrown during async execution.");
    }

    /**
     * A list of tasks to execute.
     */
    private static final class TaskList {
        /**
         * The list of all tasks.
         */
        private final Map<Integer, ScheduledTask> tasks;

        private TaskList() {
            tasks = new ConcurrentHashMap<>();
        }

        /**
         * Adds a task.
         *
         * @param task the task to remove.
         */
        private void addTask(final ScheduledTask task) {
            tasks.put(task.getTaskId(), task);
        }

        /**
         * Gets a list of all current tasks.
         *
         * @return the task list
         */
        private List<ScheduledTask> getCurrentTaskList() {
            return new ArrayList<>(tasks.values());
        }

        /**
         * Cancels a task with a specific task id.
         *
         * @param taskID the task id to cancel
         */
        private void cancelTask(final int taskID) {
            if (tasks.containsKey(taskID)) {
                final ScheduledTask task = tasks.get(taskID);
                task.cancel();
                tasks.put(taskID, task);
            }
        }
    }
}
