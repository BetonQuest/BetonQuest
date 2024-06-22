package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Locale;

/**
 * Factory to create scoreboard events from {@link Instruction}s.
 */
public class ScoreboardEventFactory implements EventFactory {
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
     * Create the scoreboard event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public ScoreboardEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String objective = instruction.next();
        final String number = instruction.next();
        final String action = instruction.getOptional("action");
        if (action != null) {
            try {
                final ScoreModification type = ScoreModification.valueOf(action.toUpperCase(Locale.ROOT));
                return new PrimaryServerThreadEvent(
                        new ScoreboardEvent(objective, new VariableNumber(instruction.getPackage(), number), type),
                        server, scheduler, plugin);
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Unknown modification action: " + instruction.current(), e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new PrimaryServerThreadEvent(
                    new ScoreboardEvent(objective, new VariableNumber(instruction.getPackage(), number.replace("*", "")), ScoreModification.MULTIPLY),
                    server, scheduler, plugin);
        }
        return new PrimaryServerThreadEvent(
                new ScoreboardEvent(objective, new VariableNumber(instruction.getPackage(), number), ScoreModification.ADD),
                server, scheduler, plugin);
    }
}
