package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
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
     */
    public LocationHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        super(loggerFactory, log);
        initialize("holograms");
    }

    @Override
    protected List<BetonHologram> getHologramsFor(final QuestPackage pack, final ConfigurationSection section) throws InstructionParseException {
        final Location location = getParsedLocation(pack, section);
        final List<BetonHologram> holograms = new ArrayList<>();
        holograms.add(HologramProvider.getInstance().createHologram(location));
        return holograms;
    }

    private Location getParsedLocation(final QuestPackage pack, final ConfigurationSection section) throws InstructionParseException {
        final String rawLocation = section.getString("location");
        if (rawLocation == null) {
            throw new InstructionParseException("Location is not specified");
        } else {
            try {
                return new CompoundLocation(pack, GlobalVariableResolver.resolve(pack, rawLocation)).getLocation(null);
            } catch (final QuestRuntimeException | InstructionParseException e) {
                throw new InstructionParseException("Could not parse location: " + e.getMessage(), e);
            }
        }
    }
}
