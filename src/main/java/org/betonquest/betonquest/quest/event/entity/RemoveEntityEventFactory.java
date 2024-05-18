package org.betonquest.betonquest.quest.event.entity;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadHybridEvent;
import org.betonquest.betonquest.quest.event.StandardHybridEventFactory;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Locale;

/**
 * Factory for {@link RemoveEntityEvent} to create from {@link Instruction}.
 * <p>
 * Created on 29.06.2018.
 */
public class RemoveEntityEventFactory extends StandardHybridEventFactory {

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
     * Creates a new KillMobEventFactory.
     *
     * @param server    the server to use for syncing to the primary server thread
     * @param scheduler the scheduler to use for syncing to the primary server thread
     * @param plugin    the plugin to use for syncing to the primary server thread
     */
    public RemoveEntityEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public HybridEvent parseHybridEvent(final Instruction instruction) throws InstructionParseException {
        final String[] entities = instruction.getArray();
        final EntityType[] types = new EntityType[entities.length];
        for (int i = 0; i < types.length; i++) {
            try {
                types[i] = EntityType.valueOf(entities[i].toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Entity type '" + entities[i] + "' does not exist", e);
            }
        }
        final CompoundLocation loc = instruction.getLocation();
        final VariableNumber range = instruction.getVarNum();
        final String name = instruction.getOptional("name");
        final boolean kill = instruction.hasArgument("kill");
        final String markedString = instruction.getOptional("marked");
        final VariableString marked = markedString == null ? null : new VariableString(instruction.getPackage(), Utils.addPackage(instruction.getPackage(), markedString));
        return new PrimaryServerThreadHybridEvent(new RemoveEntityEvent(types, loc, range, name, marked, kill),
                server, scheduler, plugin);
    }
}
