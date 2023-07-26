package org.betonquest.betonquest.quest.event.sudo;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Creates a new SudoEvent from an {@link Instruction}.
 */
public class SudoEventFactory implements EventFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

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
     * @param server    server to use
     * @param scheduler scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public SudoEventFactory(final BetonQuestLogger log, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.log = log;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String instr = instruction.getInstruction().trim();
        int index = instr.indexOf("conditions:");

        index = index == -1 ? instr.length() : index;
        final String[] commands = instr.substring(instr.indexOf(' ') + 1, index).split("\\|");
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        log, new SudoEvent(commands), instruction.getPackage()),
                server, scheduler, plugin);
    }
}
