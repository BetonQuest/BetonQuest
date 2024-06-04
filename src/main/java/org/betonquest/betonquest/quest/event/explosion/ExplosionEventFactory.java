package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory to create explosion events from {@link Instruction}s.
 */
public class ExplosionEventFactory implements ComposedEventFactory {
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
     * Create the explosion event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public ExplosionEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final boolean setsFire = "1".equals(instruction.next());
        final boolean breaksBlocks = "1".equals(instruction.next());
        final VariableNumber power = instruction.getVarNum();
        final CompoundLocation location = instruction.getLocation();
        return new PrimaryServerThreadComposedEvent(new ExplosionEvent(location, power, setsFire, breaksBlocks),
                server, scheduler, plugin);
    }
}
