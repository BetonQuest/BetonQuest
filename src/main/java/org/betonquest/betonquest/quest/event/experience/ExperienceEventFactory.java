package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Locale;

/**
 * Factory for the experience event.
 */
public class ExperienceEventFactory implements EventFactory {

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
     * Create the experience event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public ExperienceEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.PrematureDeclaration"})
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final VariableNumber amount = instruction.getVarNum();
        Experience experienceType = Experience.ADDEXPERIENCE;
        if (instruction.hasArgument("level")) {
            experienceType = Experience.ADDLEVEL;
        } else if (instruction.size() > 2) {
            final String action = instruction.getOptional("action");
            if (action == null) {
                throw new InstructionParseException("Missing modification action");
            }
            try {
                experienceType = Experience.valueOf(action.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Could not parse: '" + action +
                        "' to an experience modification typ", e);
            }
        }
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        new ExperienceEvent(experienceType, amount), instruction.getPackage()

                ), server, scheduler, plugin
        );
    }
}
