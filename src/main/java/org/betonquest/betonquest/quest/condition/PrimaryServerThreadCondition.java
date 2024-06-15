package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link Condition}s to be checked on the primary server thread.
 */
public class PrimaryServerThreadCondition extends PrimaryServerThreadType<Condition, Boolean> implements Condition {
    /**
     * Wrap the given {@link Condition} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced condition to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadCondition(final Condition synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return call(() -> synced.check(profile));
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        return call(() -> synced.check(profile));
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        return call(synced::check);
    }
}
