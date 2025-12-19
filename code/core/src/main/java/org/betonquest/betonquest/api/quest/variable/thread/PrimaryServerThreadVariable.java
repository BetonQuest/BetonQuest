package org.betonquest.betonquest.api.quest.variable.thread;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadType;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link PlayerVariable}s to be resolved on the primary server thread.
 */
public class PrimaryServerThreadVariable extends PrimaryServerThreadType<PlayerVariable, String> implements PlayerVariable {

    /**
     * Wrap the given {@link PlayerVariable} for action on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced {@link PlayerVariable} to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadVariable(final PlayerVariable synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return call(() -> synced.getValue(profile));
    }
}
