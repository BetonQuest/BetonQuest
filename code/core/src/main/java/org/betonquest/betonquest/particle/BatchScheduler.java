package org.betonquest.betonquest.particle;

import java.util.Iterator;

/**
 * A scheduler that can schedule batches of runnables to be executed with a fixed interval between each runnable.
 * <p>
 * This interface provides a way to schedule multiple tasks (runnables) to be executed sequentially
 * with a specified delay between each task. Implementations may use different scheduling mechanisms
 * such as thread-based executors or server tick schedulers.
 * <p>
 * The scheduler processes batches of runnables iteratively, executing each runnable with the specified
 * delay until all runnables in the batch have been executed.
 * <p>
 * The running scheduler may be canceled at any time to prevent further execution by executing the returned runnable.
 *
 * @see ThreadBatchScheduler
 * @see BukkitBatchScheduler
 */
@FunctionalInterface
public interface BatchScheduler {

    /**
     * Schedules a batch of runnables to be executed sequentially with a fixed delay between each execution.
     * <p>
     * The first runnable is executed immediately (or after an initial delay of 0), and each subsequent
     * runnable is executed after the specified delay from the previous execution. The batch continues
     * until all runnables from the iterator have been executed.
     *
     * @param batch       the iterator of runnables to be executed sequentially
     * @param delayMillis the delay in milliseconds between consecutive runnable executions
     * @return a runnable that when executed, cancels the scheduled batch and prevents any further
     * runnables from being executed
     */
    Runnable scheduleBatch(Iterator<Runnable> batch, long delayMillis);
}
