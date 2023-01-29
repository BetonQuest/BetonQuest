package org.betonquest.betonquest.compatibility.holograms;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Hides and shows holograms to players, based on conditions at a fixed location.
 */
@CustomLog
public class LocationHologramLoop extends HologramLoop {
    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     */
    public LocationHologramLoop() {
        super();
        initialize("holograms");
    }

    @Override
    protected List<BetonHologram> getHologramsFor(final QuestPackage pack, final ConfigurationSection section) throws InstructionParseException {
        final Location location = getParsedLocation(pack, section);
        final BetonHologram hologram = HologramProvider.getInstance().createHologram(pack.getQuestPath() + section.getCurrentPath(), location);
        return Collections.singletonList(hologram);
    }

    @Nullable
    private Location getParsedLocation(final QuestPackage pack, final ConfigurationSection section) throws InstructionParseException {
        final String rawLocation = section.getString("location");
        if (rawLocation == null) {
            throw new InstructionParseException("Location is not specified");
        } else {
            try {
                return new CompoundLocation(pack, pack.subst(rawLocation)).getLocation(null);
            } catch (QuestRuntimeException | InstructionParseException e) {
                throw new InstructionParseException("Could not parse location: " + e.getMessage(), e);
            }
        }
    }
}
