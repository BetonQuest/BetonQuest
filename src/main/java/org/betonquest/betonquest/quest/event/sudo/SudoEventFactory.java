package org.betonquest.betonquest.quest.event.sudo;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates a new SudoEvent from an {@link Instruction}.
 */
public class SudoEventFactory implements EventFactory {
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
     * Create the sudo event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler scheduler to use
     * @param plugin        plugin to use
     */
    public SudoEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final List<VariableString> commands = new ArrayList<>();
        final String string = instruction.getInstruction().trim();
        int index = string.indexOf("conditions:");
        index = index == -1 ? string.length() : index;
        final String command = (String) string.subSequence(0, index);
        // Split commands by | but allow one to use \| to represent a pipe character
        final List<String> rawCommands = Arrays.stream(command.substring(command.indexOf(' ') + 1).split("(?<!\\\\)\\|"))
                .map(s -> s.replace("\\|", "|"))
                .map(String::trim)
                .toList();
        for (final String rawCommand : rawCommands) {
            commands.add(new VariableString(instruction.getPackage(), rawCommand));
        }
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        loggerFactory.create(SudoEvent.class), new SudoEvent(commands), instruction.getPackage()),
                server, scheduler, plugin);
    }
}
