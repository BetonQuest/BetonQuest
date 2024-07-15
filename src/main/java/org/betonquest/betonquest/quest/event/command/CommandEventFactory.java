package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.bukkit.command.SilentCommandSender;
import org.betonquest.betonquest.api.bukkit.command.SilentConsoleCommandSender;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Creates a new CommandEvent from an {@link Instruction}.
 */
public class CommandEventFactory extends BaseCommandEventFactory implements StaticEventFactory {
    /**
     * Command sender to run the commands as.
     * <p>
     * {@link SilentConsoleCommandSender} is used to keep console and log clean.
     */
    private final CommandSender silentSender;

    /**
     * Server where the commands will be executed.
     */
    private final Server server;

    /**
     * Create the command event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler scheduler to use
     * @param plugin        plugin to use
     */
    public CommandEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server,
                               final BukkitScheduler scheduler, final Plugin plugin) {
        super(loggerFactory, new PrimaryServerThreadData(server, scheduler, plugin));
        this.server = server;
        this.silentSender = new SilentConsoleCommandSender(loggerFactory.create(SilentCommandSender.class,
                "CommandEvent"), server.getConsoleSender());
    }

    private ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadComposedEvent(
                new CommandEvent(parseCommands(instruction), silentSender, server), data);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return parseComposedEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return parseComposedEvent(instruction);
    }
}
