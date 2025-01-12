package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.PrimaryServerThreadType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Wrapper for {@link Event}s to be executed on the primary server thread.
 */
public class PrimaryServerThreadEvent extends PrimaryServerThreadType<Event, Void> implements Event {
    /**
     * Wrap the given {@link Event} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param syncedEvent event to synchronize
     * @param server      server for primary thread identification
     * @param scheduler   scheduler for primary thread scheduling
     * @param plugin      plugin to associate with the scheduled task
     * @deprecated use constructor with {@link PrimaryServerThreadData}
     */
    @Deprecated
    public PrimaryServerThreadEvent(final Event syncedEvent, final Server server,
                                    final BukkitScheduler scheduler, final Plugin plugin) {
        super(syncedEvent, server, scheduler, plugin);
    }

    /**
     * Wrap the given {@link Event} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param synced event to synchronize
     * @param data   the data containing server, scheduler and plugin used for primary thread access
     */
    public PrimaryServerThreadEvent(final Event synced, final PrimaryServerThreadData data) {
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
