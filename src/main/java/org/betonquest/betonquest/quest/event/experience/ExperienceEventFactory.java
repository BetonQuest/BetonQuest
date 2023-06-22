package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Locale;
import java.util.Optional;

/**
 * Factory for the experience event.
 */
public class ExperienceEventFactory implements EventFactory {
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
     * Create the experience event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public ExperienceEventFactory(final BetonQuestLogger log, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.log = log;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("PMD.PrematureDeclaration")
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final VariableNumber amount = instruction.getVarNum();
        ExperienceModification experienceType = ExperienceModification.ADD_EXPERIENCE;
        String action = instruction.getOptional("action");
        if (instruction.hasArgument("level")) {
            experienceType = ExperienceModification.ADD_LEVEL;
        } else if (action != null) {
            action = action.toUpperCase(Locale.ROOT);

            final Optional<ExperienceModification> modification = ExperienceModification.getFromInstruction(action);
            if (modification.isPresent()) {
                experienceType = modification.get();
            } else {
                throw new InstructionParseException(action + " is not a valid experience modification type.");
            }
        }
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        log, new ExperienceEvent(experienceType, amount), instruction.getPackage()
                ), server, scheduler, plugin
        );
    }
}
