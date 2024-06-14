package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlinePlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link OnlinePlayerCondition}s to be checked on the primary server thread.
 */
public class PrimaryServerThreadOnlinePlayerCondition extends PrimaryServerThreadType<OnlinePlayerCondition, Boolean> implements OnlinePlayerCondition {
    /**
     * Wrap the given {@link OnlinePlayerCondition} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced condition to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadOnlinePlayerCondition(final OnlinePlayerCondition synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return call(() -> synced.check(profile));
    }
}
