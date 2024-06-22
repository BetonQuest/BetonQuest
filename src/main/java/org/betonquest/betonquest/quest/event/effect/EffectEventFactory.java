package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory to create effect events from {@link Instruction}s.
 */
public class EffectEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

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
     * Create the effect event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler to use
     * @param plugin        plugin to use
     */
    public EffectEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final PotionEffectType effect = PotionEffectType.getByName(instruction.next());
        if (effect == null) {
            throw new InstructionParseException("Unknown effect type: " + instruction.current());
        }
        try {
            final VariableNumber duration = instruction.getVarNum();
            final VariableNumber amplifier = instruction.getVarNum();
            final boolean ambient = instruction.hasArgument("ambient");
            final boolean hidden = instruction.hasArgument("hidden");
            final boolean icon = !instruction.hasArgument("noicon");
            return new PrimaryServerThreadEvent(new OnlineProfileRequiredEvent(
                    loggerFactory.create(EffectEvent.class), new EffectEvent(effect, duration, amplifier, ambient, hidden, icon), instruction.getPackage()),
                    server, scheduler, plugin);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Could not parse effect duration and amplifier", e);
        }
    }
}
