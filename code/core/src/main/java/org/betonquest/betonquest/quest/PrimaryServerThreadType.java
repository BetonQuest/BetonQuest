package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.common.function.QuestCallable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
     * Data wrapper containing server, scheduler and plugin for primary thread identification and access.
     */
    protected final PrimaryServerThreadData data;

    /**
     * Wrap the given {@link T} for action on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced {@link T} to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadType(final T synced, final PrimaryServerThreadData data) {
        this.synced = synced;
        this.data = data;
    }

    /**
     * Calls the quest type on the main thread.
     *
     * @param execute the wrapped quest type method call
     * @return result of the call
     * @throws QuestException when an QuestException is thrown during event execution
     */
    protected R call(final QuestCallable<R> execute) throws QuestException {
        if (data.server().isPrimaryThread()) {
            return execute.call();
        } else {
            return executeOnPrimaryThread(execute::call);
        }
    }

    /**
     * Executes the {@link T} on the primary thread and gets it return value {@link R}.
     *
     * @param callable the method of {@link T}
     * @return the return value {@link R} of the callable
     * @throws QuestException when the callable gets interrupted or another exception occurs during the execution
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    private R executeOnPrimaryThread(final Callable<R> callable) throws QuestException {
        final Future<R> executingEventFuture = data.scheduler().callSyncMethod(data.plugin(), callable);
        try {
            return executingEventFuture.get(10, TimeUnit.SECONDS);
        } catch (final InterruptedException | TimeoutException e) {
            executingEventFuture.cancel(true);
            throw new QuestException("Thread was Interrupted!", e);
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof final QuestException cause) {
                throw cause;
            }
            throw new QuestException(e);
        }
    }
}
