package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Class containing the server, scheduler and plugin used for syncing with the main thread.
 *
 * @param <T> the condition category
 */
public class PrimaryServerThreadConditionFrame<T> extends PrimaryServerThreadType<T, Boolean> {
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
    public PrimaryServerThreadConditionFrame(final T synced, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        super(synced, server, scheduler, plugin);
    }

    /**
     * Executes an event on the main thread.
     *
     * @param questConditionChecker the wrapped event execution call
     * @return result of the condition check
     * @throws QuestRuntimeException when a QuestRuntimeException is thrown during event execution
     */
    protected boolean check(final QuestConditionChecker questConditionChecker) throws QuestRuntimeException {
        if (server.isPrimaryThread()) {
            return questConditionChecker.check();
        } else {
            return executeOnPrimaryThread(questConditionChecker::check);
        }
    }

    /**
     * Calls the check method that may throw a {@link QuestRuntimeException}.
     */
    protected interface QuestConditionChecker {
        /**
         * Checks the condition.
         *
         * @return result of the check
         * @throws QuestRuntimeException when a QuestRuntimeException is thrown during condition checking
         */
        boolean check() throws QuestRuntimeException;
    }
}
