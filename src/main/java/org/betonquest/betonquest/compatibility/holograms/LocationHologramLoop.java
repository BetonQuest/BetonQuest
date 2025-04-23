package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Hides and shows holograms to players, based on conditions at a fixed location.
 */
public class LocationHologramLoop extends HologramLoop {
    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     *
     * @param loggerFactory logger factory to use
     * @param log           the logger that will be used for logging
     */
    public LocationHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        super(loggerFactory, log);
        initialize("holograms");
    }

    @Override
    protected List<BetonHologram> getHologramsFor(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Location location = getParsedLocation(pack, section);
        final List<BetonHologram> holograms = new ArrayList<>();
        holograms.add(HologramProvider.getInstance().createHologram(location));
        return holograms;
    }

    private Location getParsedLocation(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final String rawLocation = section.getString("location");
        if (rawLocation == null) {
            throw new QuestException("Location is not specified");
        } else {
            final String rawLoc = GlobalVariableResolver.resolve(pack, rawLocation);
            return new Variable<>(BetonQuest.getInstance().getVariableProcessor(), pack, rawLoc, Argument.LOCATION).getValue(null);
        }
    }
}
