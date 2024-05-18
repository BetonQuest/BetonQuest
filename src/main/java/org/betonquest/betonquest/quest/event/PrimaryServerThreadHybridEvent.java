package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link HybridEvent}s to be executed on the primary server thread.
 */
public class PrimaryServerThreadHybridEvent extends PrimaryServerThreadEventFrame<HybridEvent> implements HybridEvent {
    /**
     * Wrap the given {@link HybridEvent} for execution on the primary server thread.
     * The {@link Server}, {@link BukkitScheduler} and {@link Plugin} are used to
     * determine if the current thread is the primary server thread and to
     * schedule the execution onto it in case it isn't.
     *
     * @param syncedEvent event to synchronize
     * @param server      server for primary thread identification
     * @param scheduler   scheduler for primary thread scheduling
     * @param plugin      plugin to associate with the scheduled task
     */
    public PrimaryServerThreadHybridEvent(final HybridEvent syncedEvent, final Server server,
                                          final BukkitScheduler scheduler, final Plugin plugin) {
        super(syncedEvent, server, scheduler, plugin);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        execute(() -> syncedEvent.execute(profile));
    }

    @Override
    public void execute() throws QuestRuntimeException {
        execute(syncedEvent::execute);
    }
}
