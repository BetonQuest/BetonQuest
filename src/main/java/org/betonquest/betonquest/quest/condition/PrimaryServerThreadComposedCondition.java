package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.ComposedCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link ComposedCondition}s to be checked on the primary server thread.
 */
public class PrimaryServerThreadComposedCondition extends PrimaryServerThreadConditionFrame<ComposedCondition> implements ComposedCondition {
    /**
     * Wrap the given {@link ComposedCondition} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced    condition to synchronize
     * @param server    server for primary thread identification
     * @param scheduler scheduler for primary thread scheduling
     * @param plugin    plugin to associate with the scheduled task
     */
    public PrimaryServerThreadComposedCondition(final ComposedCondition synced, final Server server,
                                              final BukkitScheduler scheduler, final Plugin plugin) {
        super(synced, server, scheduler, plugin);
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        return check(() -> synced.check(profile));
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        return check(synced::check);
    }
}
