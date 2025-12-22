package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.kernel.processor.StartTask;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Hides and shows holograms to players, based on conditions at a fixed location.
 */
public class LocationHologramLoop extends HologramLoop implements StartTask {

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     *
     * @param loggerFactory    logger factory to use
     * @param log              the logger that will be used for logging
     * @param variables        the variable processor to create and resolve variables
     * @param packManager      the quest package manager to get quest packages from
     * @param hologramProvider the hologram provider to create new holograms
     * @param plugin           the plugin to start tasks
     * @param textParser        the text parser used to parse text and colors
     */
    public LocationHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                                final Variables variables, final QuestPackageManager packManager,
                                final HologramProvider hologramProvider, final Plugin plugin, final TextParser textParser) {
        super(loggerFactory, log, variables, packManager, hologramProvider, "Hologram", "holograms", textParser);
        this.plugin = plugin;
    }

    @Override
    protected List<BetonHologram> getHologramsFor(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Location location = getParsedLocation(pack, section);
        final List<BetonHologram> holograms = new ArrayList<>();
        holograms.add(hologramProvider.createHologram(location));
        return holograms;
    }

    private Location getParsedLocation(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final String rawLocation = section.getString("location");
        if (rawLocation == null) {
            throw new QuestException("Location is not specified");
        } else {
            return new Variable<>(variables, pack, rawLocation, Argument.LOCATION).getValue(null);
        }
    }

    @Override
    public void startAll() {
        HologramRunner.start(plugin);
    }
}
