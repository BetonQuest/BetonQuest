package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create delete effect events from {@link Instruction}s.
 */
public class DeleteEffectEventFactory implements EventFactory {
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
     * Create the delete effect event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler to use
     * @param plugin        plugin to use
     */
    public DeleteEffectEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        List<PotionEffectType> effects = Collections.emptyList();
        if (!instruction.hasArgument("any") && instruction.size() > 1) {
            effects = instruction.getList(type -> {
                final PotionEffectType effect = PotionEffectType.getByName(type);
                if (effect == null) {
                    throw new InstructionParseException("Unknown effect type: " + type);
                } else {
                    return effect;
                }
            });
        }
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        loggerFactory.create(DeleteEffectEvent.class), new DeleteEffectEvent(effects), instruction.getPackage()), server, scheduler, plugin);
    }
}
