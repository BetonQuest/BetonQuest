package org.betonquest.betonquest.utils.location;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * This class parses various location related strings with or without {@link Variable}s.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CompoundLocation {
    private final LocationData locationData;
    private final VectorData vectorData;

    /**
     * This class parses a string into a {@link Location} and a {@link Vector}. The input string has
     * to be in the format 'x;y;z;world[;yaw;pitch][-&gt; (x;y;z)]'. All elements in square brackets are optional.
     * The last optional part is the {@link Vector} that will be added to the {@link Location} if specified.
     * Each part of the input string can be a {@link Variable} instead of an {@link Integer} or {@link String}.
     *
     * @param packName Name of the {@link ConfigPackage} - required for {@link Variable} resolution
     * @param data     string containing raw location in the defined format
     * @throws InstructionParseException Is thrown when an error appears while parsing {@link LocationData}
     *                                   or {@link VectorData}
     */
    public CompoundLocation(final String packName, final String data) throws InstructionParseException {

        if (data.contains("->")) {
            final String[] parts = data.split("->");
            locationData = new LocationData(packName, parts[0]);
            vectorData = new VectorData(packName, parts[1]);
        } else {
            locationData = new LocationData(packName, data);
            vectorData = null;
        }
    }

    /**
     * @param playerID ID of the player - needed for location resolution
     * @return the location represented by this object
     * @throws QuestRuntimeException Is thrown when the player cannot be accessed or the the resolved location is in
     *                               the wrong format.
     */
    public Location getLocation(final String playerID) throws QuestRuntimeException {
        final Location loc = locationData.get(playerID);
        final Vector vec = vectorData == null ? new Vector() : vectorData.get(playerID);
        return loc.clone().add(vec);
    }

    public LocationData getLocationData() {
        return locationData;
    }

    public VectorData getVectorData() {
        return vectorData;
    }
}
