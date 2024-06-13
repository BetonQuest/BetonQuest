package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link PlayerlessCondition}s to be checked on the primary server thread.
 */
public class PrimaryServerThreadPlayerlessCondition extends PrimaryServerThreadType<PlayerlessCondition, Boolean> implements PlayerlessCondition {
    /**
     * Wrap the given {@link PlayerlessCondition} for action on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced    condition to synchronize
     * @param server    server for primary thread identification
     * @param scheduler scheduler for primary thread scheduling
     * @param plugin    plugin to associate with the scheduled task
     */
    public PrimaryServerThreadPlayerlessCondition(final PlayerlessCondition synced, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        super(synced, server, scheduler, plugin);
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        return call(synced::check);
    }
}
