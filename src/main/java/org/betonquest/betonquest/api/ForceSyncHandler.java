package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A utility class to force sync calls on the main thread.
 *
 * @param <T> Return type of the {@link ForceSyncHandler#execute(Profile)} method (This could also be {@link Void}).
 */
abstract public class ForceSyncHandler<T> {
    /**
     * Whether the {@link ForceSyncHandler} forces a sync execution.
     */
    private final boolean forceSync;

    /**
     * Initializes the {@link ForceSyncHandler}
     *
     * @param forceSync If set to true the extending classes
     *                  {@link ForceSyncHandler#execute(Profile)} method will run on the servers main thread.
     *                  If set to false the {@link ForceSyncHandler#execute(Profile)} method will run on the current thread.
     */
    public ForceSyncHandler(final boolean forceSync) {
        this.forceSync = forceSync;
    }

    /**
     * This methods implementation will be executed by the {@link ForceSyncHandler#handle(Profile)} method.
     *
     * @param profile the {@link Profile} the event is executed for
     * @return Return the corresponding ForceSyncHandler type.
     * @throws QuestRuntimeException Is thrown if something unexpected happens.
     */
    protected abstract T execute(Profile profile) throws QuestRuntimeException;

    /**
     * If {@link ForceSyncHandler#forceSync} is set to true the extending classes
     * {@link ForceSyncHandler#execute(Profile)} method will run on the servers main thread.
     * If set to false the {@link ForceSyncHandler#execute(Profile)} method will run on the current thread.
     *
     * @param profile the {@link Profile} the event is handled for
     * @return {@link ForceSyncHandler#execute(Profile)}'s return value.
     * @throws QuestRuntimeException Either the QRE from the implemented {@link ForceSyncHandler#execute(Profile)}
     *                               or from {@link ForceSyncHandler#handle(Profile)}'s
     *                               {@link org.bukkit.scheduler.BukkitScheduler#callSyncMethod(Plugin, Callable)} call.
     */
    public T handle(final Profile profile) throws QuestRuntimeException {
        if (forceSync && !Bukkit.isPrimaryThread()) {
            final Future<T> returnFuture = Bukkit.getScheduler().callSyncMethod(BetonQuest.getInstance(), () -> execute(profile));
            try {
                return returnFuture.get();
            } catch (final InterruptedException e) {
                throw new QuestRuntimeException("Thread was Interrupted!", e);
            } catch (final ExecutionException e) {
                if (e.getCause() instanceof QuestRuntimeException) {
                    throw (QuestRuntimeException) e.getCause();
                }
                throw new QuestRuntimeException(e);
            }
        } else {
            return execute(profile);
        }
    }
}
