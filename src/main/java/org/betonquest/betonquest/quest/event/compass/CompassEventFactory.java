package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * The compass event factory.
 */
public class CompassEventFactory implements EventFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * BetonQuest instance to use to get the event factory.
     */
    private final BetonQuest betonQuest;

    /**
     * Plugin manager to use to call the event.
     */
    private final PluginManager pluginManager;

    /**
     * Server to check if the current thread is the primary server thread.
     */
    private final Server server;

    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;

    /**
     * Create the compass event factory.
     *
     * @param loggerFactory logger factory to use
     * @param betonQuest    betonQuest instance to use
     * @param pluginManager plugin manager to use
     * @param server        server to use
     * @param scheduler     scheduler to use
     */
    public CompassEventFactory(final BetonQuestLoggerFactory loggerFactory, final BetonQuest betonQuest,
                               final PluginManager pluginManager, final Server server, final BukkitScheduler scheduler) {
        this.log = loggerFactory.create(CompassEvent.class);
        this.betonQuest = betonQuest;
        this.pluginManager = pluginManager;
        this.server = server;
        this.scheduler = scheduler;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final CompassTargetAction action = instruction.getEnum(CompassTargetAction.class);
        final String compass = instruction.next();
        final CompoundLocation compassLocation = getCompassLocation(compass);
        return new PrimaryServerThreadEvent(
                new CompassEvent(log, betonQuest, pluginManager, action, compass, compassLocation, instruction.getPackage()),
                server, scheduler, betonQuest);
    }

    private CompoundLocation getCompassLocation(final String compass) throws InstructionParseException {
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("compass");
            if (section != null && section.contains(compass)) {
                return new CompoundLocation(pack, Utils.getNN(pack.getString("compass." + compass + ".location"),
                        "Missing location in compass section"));
            }
        }
        throw new InstructionParseException("Invalid compass location: " + compass);
    }
}
