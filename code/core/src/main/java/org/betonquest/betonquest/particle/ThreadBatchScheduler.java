package org.betonquest.betonquest.particle;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A thread-based scheduler that executes batches of tasks using a dedicated thread.
 * <p>
 * This implementation uses a {@link ScheduledExecutorService} to execute runnables with a specified
 * delay between each task. Each batch runs on a separate single-threaded executor that automatically
 * shuts down after all tasks have been executed.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class ThreadBatchScheduler implements BatchScheduler {

    /**
     * Create a new {@link ThreadBatchScheduler}.
     */
    public ThreadBatchScheduler() {
    }

    @Override
    public Runnable scheduleBatch(final Iterator<Runnable> batch, final long delayMillis) {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        final Runnable batchRunner = () -> {
            if (batch.hasNext()) {
                batch.next().run();
            } else {
                executorService.shutdown();
            }
        };
        executorService.scheduleAtFixedRate(batchRunner, 0, delayMillis, TimeUnit.MILLISECONDS);
        return executorService::shutdown;
    }
}
