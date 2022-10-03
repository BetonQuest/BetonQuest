package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
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
 */
public class PrimaryServerThreadEvent implements Event {
    /**
     * Event to be executed on the primary server thread.
     */
    private final Event syncedEvent;

    /**
     * Server to use to determine if currently on the primary server thread.
     */
    private final Server server;

    /**
     * Scheduler for scheduling the event to be executed on the primary server thread.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin to associate the scheduled task with.
     */
    private final Plugin plugin;

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
    public PrimaryServerThreadEvent(final Event syncedEvent, final Server server,
                                    final BukkitScheduler scheduler, final Plugin plugin) {
        this.syncedEvent = syncedEvent;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        if (server.isPrimaryThread()) {
            syncedEvent.execute(profile);
        } else {
            executeOnPrimaryThread(() -> {
                syncedEvent.execute(profile);
                return null;
            });
        }
    }

    private void executeOnPrimaryThread(final Callable<Void> callable) throws QuestRuntimeException {
        final Future<Void> executingEventFuture = scheduler.callSyncMethod(plugin, callable);
        try {
            executingEventFuture.get();
        } catch (final InterruptedException e) {
            executingEventFuture.cancel(true);
            throw new QuestRuntimeException("Thread was Interrupted!", e);
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof QuestRuntimeException) {
                throw (QuestRuntimeException) e.getCause();
            }
            throw new QuestRuntimeException(e);
        }
    }
}
