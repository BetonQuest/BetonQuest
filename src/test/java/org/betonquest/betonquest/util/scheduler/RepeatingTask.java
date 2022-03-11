package org.betonquest.betonquest.util.scheduler;

import org.bukkit.plugin.Plugin;

/**
 * A scheduled repeating task for the {@link BukkitSchedulerMock}.
 */
public class RepeatingTask extends ScheduledTask {
    /**
     * Period to execute the {@link ScheduledTask}.
     */
    private final long period;

    /**
     * Creates a new scheduled repeating task.
     *
     * @param taskId        the task id
     * @param plugin        the plugin from the task
     * @param sync          is the task sync
     * @param scheduledTick the tick when to execute the task
     * @param period        the period to execute this task
     * @param runnable      the runnable to execute
     */
    public RepeatingTask(final int taskId, final Plugin plugin, final boolean sync, final long scheduledTick, final long period, final Runnable runnable) {
        super(taskId, plugin, sync, scheduledTick, runnable);
        this.period = period;
    }

    /**
     * Gets the period of the timer.
     *
     * @return The period of the timer.
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Updates the scheduled tick for the next run.
     */
    public void updateScheduledTick() {
        setScheduledTick(getScheduledTick() + period);
    }
}
