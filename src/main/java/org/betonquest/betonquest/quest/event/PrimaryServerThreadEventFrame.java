package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Wrapper for {@link Event}s to be executed on the primary server thread.
 *
 * @param <T> the event category
 */
public class PrimaryServerThreadEventFrame<T> {
    /**
     * Event to be executed on the primary server thread.
     */
    protected final T syncedEvent;

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
     * Wrap the given {@link Event} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param syncedEvent event to synchronize
     * @param server      server for primary thread identification
     * @param scheduler   scheduler for primary thread scheduling
     * @param plugin      plugin to associate with the scheduled task
     */
    public PrimaryServerThreadEventFrame(final T syncedEvent, final Server server,
                                         final BukkitScheduler scheduler, final Plugin plugin) {
        this.syncedEvent = syncedEvent;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Executes an event on the main thread.
     *
     * @param questEventExecutor the wrapped event execution call
     * @throws QuestRuntimeException when a QuestRuntimeException is thrown during event execution
     */
    protected void execute(final QuestEventExecutor questEventExecutor) throws QuestRuntimeException {
        if (server.isPrimaryThread()) {
            questEventExecutor.execute();
        } else {
            executeOnPrimaryThread(() -> {
                questEventExecutor.execute();
                return null;
            });
        }
    }

    /**
     * Calls the event on the main thread.
     *
     * @param callable the event execution method
     * @throws QuestRuntimeException when the call gets interrupted
     * @throws QuestRuntimeException when a QuestRuntimeException is thrown during event execution
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    private void executeOnPrimaryThread(final Callable<Void> callable) throws QuestRuntimeException {
        final Future<Void> executingEventFuture = scheduler.callSyncMethod(plugin, callable);
        try {
            executingEventFuture.get();
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

    /**
     * Calls the execute method that may throw a {@link QuestRuntimeException}.
     */
    protected interface QuestEventExecutor {
        /**
         * Executes the event.
         *
         * @throws QuestRuntimeException when a QuestRuntimeException is thrown during event execution
         */
        void execute() throws QuestRuntimeException;
    }
}
