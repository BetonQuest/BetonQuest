package org.betonquest.betonquest.api.quest.variable.thread;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadType;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link NullableVariable}s to be resolved on the primary server thread.
 */
public class PrimaryServerThreadNullableVariable extends PrimaryServerThreadType<NullableVariable, String> implements NullableVariable {

    /**
     * Wrap the given {@link NullableVariable} for action on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced {@link NullableVariable} to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadNullableVariable(final NullableVariable synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return call(() -> synced.getValue(profile));
    }
}
