package org.betonquest.betonquest.api.quest.event.thread;

import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadType;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link PlayerlessEvent}s to be executed on the primary server thread.
 */
public final class PrimaryServerThreadPlayerlessEvent extends PrimaryServerThreadType<PlayerlessEvent, Void> implements PlayerlessEvent {
    /**
     * Wrap the given {@link PlayerlessEvent} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced event to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadPlayerlessEvent(final PlayerlessEvent synced, final PrimaryServerThreadData data) {
        super(synced, data);
    }

    @SuppressWarnings("NullAway") // FalsePositive, see https://github.com/uber/NullAway/issues/801
    @Override
    public void execute() throws QuestException {
        call(() -> {
            synced.execute();
            return null;
        });
    }
}
