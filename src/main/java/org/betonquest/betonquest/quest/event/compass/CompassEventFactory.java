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
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;

/**
 * The compass event factory.
 */
public class CompassEventFactory implements EventFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * BetonQuest instance to get the offline player data.
     */
    private final BetonQuest betonQuest;

    /**
     * Plugin manager to use to call the event.
     */
    private final PluginManager pluginManager;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the compass event factory.
     *
     * @param loggerFactory logger factory to use
     * @param betonQuest    betonQuest instance to use
     * @param pluginManager plugin manager to use
     * @param data          the data for primary server thread access
     */
    public CompassEventFactory(final BetonQuestLoggerFactory loggerFactory, final BetonQuest betonQuest,
                               final PluginManager pluginManager, final PrimaryServerThreadData data) {
        this.log = loggerFactory.create(CompassEvent.class);
        this.betonQuest = betonQuest;
        this.pluginManager = pluginManager;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final CompassTargetAction action = instruction.getEnum(CompassTargetAction.class);
        final String compass = instruction.next();
        final VariableLocation compassLocation = getCompassLocation(compass);
        return new PrimaryServerThreadEvent(
                new CompassEvent(log, betonQuest, pluginManager, action, compass, compassLocation, instruction.getPackage()),
                data);
    }

    private VariableLocation getCompassLocation(final String compass) throws InstructionParseException {
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("compass");
            if (section != null && section.contains(compass)) {
                return new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), pack,
                        Utils.getNN(pack.getString("compass." + compass + ".location"),
                                "Missing location in compass section"));
            }
        }
        throw new InstructionParseException("Invalid compass location: " + compass);
    }
}
