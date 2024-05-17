package org.betonquest.betonquest.compatibility.citizens.events.move;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.CallStaticEventAdapter;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory for {@link CitizensStopEvent} from the {@link Instruction}.
 */
public class CitizensStopEventFactory implements EventFactory, StaticEventFactory {
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
     * Move Controller where to stop the NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new NPCTeleportEventFactory.
     *
     * @param server                 the server to use for syncing to the primary server thread
     * @param scheduler              the scheduler to use for syncing to the primary server thread
     * @param plugin                 the plugin to use for syncing to the primary server thread
     * @param citizensMoveController the move controller where to stop the NPC movement
     */
    public CitizensStopEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin, final CitizensMoveController citizensMoveController) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        final int npcId = instruction.getInt();
        return new PrimaryServerThreadStaticEvent(new CitizensStopEvent(npcId, citizensMoveController), server, scheduler, plugin);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new CallStaticEventAdapter(parseStaticEvent(instruction));
    }
}
