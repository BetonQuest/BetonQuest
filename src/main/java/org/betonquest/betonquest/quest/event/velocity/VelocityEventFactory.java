package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlinePlayerRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.utils.location.VectorData;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory to create velocity events from {@link Instruction}s.
 */
public class VelocityEventFactory implements EventFactory {
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
     * Create the velocity event factory
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public VelocityEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String rawVector = instruction.getOptional("vector");
        if (rawVector == null) {
            throw new InstructionParseException("A 'vector' is required");
        }
        final VectorData vector = new VectorData(instruction.getPackage().getQuestPath(), rawVector);
        final VectorDirection direction = instruction.getEnum(instruction.getOptional("direction"), VectorDirection.class, VectorDirection.ABSOLUTE);
        final VectorModification modification = instruction.getEnum(instruction.getOptional("modification"), VectorModification.class, VectorModification.SET);
        return new PrimaryServerThreadEvent(
                new OnlinePlayerRequiredEvent(
                        new VelocityEvent(vector, direction, modification),
                        instruction.getPackage()),
                server, scheduler, plugin);
    }
}
