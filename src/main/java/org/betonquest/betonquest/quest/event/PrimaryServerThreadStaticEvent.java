package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link StaticEvent}s to be executed on the primary server thread.
 */
public class PrimaryServerThreadStaticEvent extends PrimaryServerThreadType<StaticEvent, Void> implements StaticEvent {
    /**
     * Wrap the given {@link StaticEvent} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced event to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadStaticEvent(final StaticEvent synced, final PrimaryServerThreadData data) {
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
