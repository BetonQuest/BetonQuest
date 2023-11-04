package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for command related event factories.
 * <p>
 * Contains common methods for parsing commands from instructions and holds the server, scheduler and plugin.
 */
public abstract class BaseCommandEventFactory implements EventFactory {

    /**
     * Server to use for syncing to the primary server thread.
     */
    protected final Server server;

    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    protected final BukkitScheduler scheduler;

    /**
     * Plugin to use for syncing to the primary server thread.
     */
    protected final Plugin plugin;

    /**
     * Logger factory to create a logger for events.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the sudo event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler scheduler to use
     * @param plugin        plugin to use
     */
    public BaseCommandEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server,
                                   final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Parses the commands from the instruction.
     * <p>
     * The commands are separated by | but one can use \| to represent a pipe character.
     * If the instruction contains a conditions: section, it will be trimmed from the command string.
     *
     * @param instruction instruction to parse
     * @return list of commands
     * @throws InstructionParseException if the instruction is invalid
     */
    public List<VariableString> parseCommands(final Instruction instruction) throws InstructionParseException {
        final List<VariableString> commands = new ArrayList<>();
        final String string = instruction.getInstruction().trim();
        int index = string.indexOf("conditions:");
        index = index == -1 ? string.length() : index;
        final String command = (String) string.subSequence(0, index);
        final List<String> rawCommands = Arrays.stream(command.substring(command.indexOf(' ') + 1).split("(?<!\\\\)\\|"))
                .map(s -> s.replace("\\|", "|"))
                .map(String::trim)
                .toList();
        for (final String rawCommand : rawCommands) {
            commands.add(new VariableString(instruction.getPackage(), rawCommand));
        }
        return commands;
    }
}
