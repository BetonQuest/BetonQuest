package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link PlayerEvent}s to be executed on the primary server thread.
 */
public class PrimaryServerThreadEvent extends PrimaryServerThreadType<PlayerEvent, Void> implements PlayerEvent {

    /**
     * Wrap the given {@link PlayerEvent} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced event to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadEvent(final PlayerEvent synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @SuppressWarnings("NullAway") // FalsePositive, see https://github.com/uber/NullAway/issues/801
    @Override
    public void execute(final Profile profile) throws QuestException {
        call(() -> {
            synced.execute(profile);
            return null;
        });
    }
}
