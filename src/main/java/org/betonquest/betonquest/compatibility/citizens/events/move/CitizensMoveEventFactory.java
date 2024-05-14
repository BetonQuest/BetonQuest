package org.betonquest.betonquest.compatibility.citizens.events.move;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

/**
 * Factory for {@link CitizensMoveEvent} from the {@link Instruction}.
 */
public class CitizensMoveEventFactory implements EventFactory {
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
     * Move instance to handle movement of Citizens NPCs.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new NPCTeleportEventFactory.
     *
     * @param server                 the server to use for syncing to the primary server thread
     * @param scheduler              the scheduler to use for syncing to the primary server thread
     * @param plugin                 the plugin to use for syncing to the primary server thread
     * @param citizensMoveController the move instance to handle movement of Citizens NPCs
     */
    public CitizensMoveEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin, final CitizensMoveController citizensMoveController) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    @SuppressWarnings("PMD.PrematureDeclaration")
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final int npcId = instruction.getInt();
        final List<CompoundLocation> locations = instruction.getList(instruction::getLocation);
        if (locations.isEmpty()) {
            throw new InstructionParseException("Not enough arguments");
        }
        final int waitTicks = instruction.getInt(instruction.getOptional("wait"), 0);
        final EventID[] doneEvents = instruction.getList(instruction.getOptional("done"), instruction::getEvent).toArray(new EventID[0]);
        final EventID[] failEvents = instruction.getList(instruction.getOptional("fail"), instruction::getEvent).toArray(new EventID[0]);
        final boolean blockConversations = instruction.hasArgument("block");
        final CitizensMoveController.MoveData moveAction = new CitizensMoveController.MoveData(locations, waitTicks, doneEvents, failEvents, blockConversations, instruction.getPackage());
        return new PrimaryServerThreadEvent(new CitizensMoveEvent(npcId, citizensMoveController, moveAction),
                server, scheduler, plugin);
    }
}
