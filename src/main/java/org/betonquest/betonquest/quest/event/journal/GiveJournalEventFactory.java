package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Creates a new GiveJournalEvent from an {@link Instruction}.
 */
public class GiveJournalEventFactory implements EventFactory {

    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;
    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;
    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;

    /**
     * Create the give journal event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public GiveJournalEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        new GiveJournalEvent(), instruction.getPackage())
                , server, scheduler, plugin);
    }
}
