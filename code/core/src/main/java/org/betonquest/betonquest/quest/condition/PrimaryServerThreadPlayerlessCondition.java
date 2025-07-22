package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
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
     * @param synced {@link PlayerlessCondition} to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadPlayerlessCondition(final PlayerlessCondition synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @Override
    public boolean check() throws QuestException {
        return call(synced::check);
    }
}
