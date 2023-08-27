package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory for {@link ConversationEvent}.
 */
public class ConversationEventFactory implements EventFactory {
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
     * Create the conversation event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler to use
     * @param plugin        plugin to use
     */
    public ConversationEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String conversation = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        loggerFactory.create(ConversationEventFactory.class), new ConversationEvent(loggerFactory, conversation), instruction.getPackage()
                ), server, scheduler, plugin
        );
    }
}
