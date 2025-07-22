package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link PlayerCondition}s to be checked on the primary server thread.
 */
public class PrimaryServerThreadPlayerCondition extends PrimaryServerThreadType<PlayerCondition, Boolean> implements PlayerCondition {
    /**
     * Wrap the given {@link PlayerCondition} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced condition to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadPlayerCondition(final PlayerCondition synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return call(() -> synced.check(profile));
    }
}
