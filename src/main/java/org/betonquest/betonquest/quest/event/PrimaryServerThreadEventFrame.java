package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link Event}s to be executed on the primary server thread.
 *
 * @param <T> the event category
 */
public class PrimaryServerThreadEventFrame<T> extends PrimaryServerThreadType<T, Void> {
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
        super(syncedEvent, server, scheduler, plugin);
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
