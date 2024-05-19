package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Quest type wrapper to execute the functionality on the server main thread.
 *
 * @param <T> quest type which should be used
 * @param <R> return type of the method to execute
 */
public class PrimaryServerThreadType<T, R> {
    /**
     * {@link T} to be actioned on the primary server thread.
     */
    protected final T synced;

    /**
     * Server to use to determine if currently on the primary server thread.
     */
    protected final Server server;

    /**
     * Scheduler for scheduling the event to be executed on the primary server thread.
     */
    protected final BukkitScheduler scheduler;

    /**
     * Plugin to associate the scheduled task with.
     */
    protected final Plugin plugin;

    /**
     * Wrap the given {@link T} for action on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced    {@link T} to synchronize
     * @param server    server for primary thread identification
     * @param scheduler scheduler for primary thread scheduling
     * @param plugin    plugin to associate with the scheduled task
     */
    public PrimaryServerThreadType(final T synced, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.synced = synced;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Executes the {@link T} on the primary thread and gets it return value {@link R}.
     *
     * @param callable the method of {@link T}
     * @return the return value {@link R} of the callable
     * @throws QuestRuntimeException when the callable gets interrupted or another exception occurs during the execution
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    protected R executeOnPrimaryThread(final Callable<R> callable) throws QuestRuntimeException {
        final Future<R> executingEventFuture = scheduler.callSyncMethod(plugin, callable);
        try {
            return executingEventFuture.get();
        } catch (final InterruptedException e) {
            executingEventFuture.cancel(true);
            throw new QuestRuntimeException("Thread was Interrupted!", e);
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof final QuestRuntimeException cause) {
                throw cause;
            }
            throw new QuestRuntimeException(e);
        }
    }
}
