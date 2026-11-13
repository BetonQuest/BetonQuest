package org.betonquest.betonquest.particle;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;

/**
 * A Bukkit-based scheduler that executes batches of tasks using the Bukkit scheduler.
 * <p>
 * This implementation allows execution of runnables with a specified delay between each task,
 * either synchronously on the main server thread or asynchronously on a separate thread.
 * <p>
 * Due to limitations in the Bukkit scheduler, the delay is rounded down to the nearest 50ms.
 */
public class BukkitBatchScheduler implements BatchScheduler {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Whether to run the tasks in sync.
     */
    private final boolean runInSync;

    /**
     * Create a new {@link BukkitBatchScheduler}.
     *
     * @param plugin    the plugin instance
     * @param runInSync whether to run the tasks in sync
     */
    public BukkitBatchScheduler(final Plugin plugin, final boolean runInSync) {
        this.plugin = plugin;
        this.runInSync = runInSync;
    }

    @Override
    public Runnable scheduleBatch(final Iterator<Runnable> batch, final long delayMillis) {
        final long delayTicks = Math.max(1, delayMillis / 50);
        final BukkitRunnable batchRunner = new BukkitRunnable() {

            @Override
            public void run() {
                if (batch.hasNext()) {
                    final Runnable nextElement = batch.next();
                    nextElement.run();
                } else {
                    cancel();
                }
            }
        };
        final BukkitTask task = runInSync ? batchRunner.runTaskTimer(plugin, 0, delayTicks) : batchRunner.runTaskTimerAsynchronously(plugin, 0, delayTicks);
        return task::cancel;
    }
}
