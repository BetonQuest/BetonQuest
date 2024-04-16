package org.betonquest.betonquest.utils.location;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * This class parses various location related strings with or without {@link Variable}s.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CompoundLocation {
    private final LocationData locationData;

    @Nullable
    private final VectorData vectorData;

    /**
     * This class parses a string into a {@link Location} and a {@link Vector}. The input string has
     * to be in the format 'x;y;z;world[;yaw;pitch][-&gt; (x;y;z)]'. All elements in square brackets are optional.
     * The last optional part is the {@link Vector} that will be added to the {@link Location} if specified.
     * Each part of the input string can be a {@link Variable} instead of an {@link Integer} or {@link String}.
     *
     * @param pack Name of the {@link QuestPackage} - required for {@link Variable} resolution
     * @param data string containing raw location in the defined format
     * @throws InstructionParseException Is thrown when an error appears while parsing {@link LocationData}
     *                                   or {@link VectorData}
     */
    public CompoundLocation(@Nullable final QuestPackage pack, final String data) throws InstructionParseException {
        if (data.contains("->")) {
            final String[] parts = data.split("->");
            locationData = new LocationData(pack, parts[0]);
            vectorData = new VectorData(pack, parts[1]);
        } else {
            locationData = new LocationData(pack, data);
            vectorData = null;
        }
    }

    /**
     * This class parses a string into a {@link Location} and a {@link Vector}. The input string has
     * to be in the format 'x;y;z;world[;yaw;pitch][-&gt; (x;y;z)]'. All elements in square brackets are optional.
     * The last optional part is the {@link Vector} that will be added to the {@link Location} if specified.
     * Each part of the input string can be a {@link Variable} instead of an {@link Integer} or {@link String}.
     *
     * @param packName Name of the {@link QuestPackage} - required for {@link Variable} resolution
     * @param data     string containing raw location in the defined format
     * @throws InstructionParseException Is thrown when an error appears while parsing {@link LocationData}
     *                                   or {@link VectorData}
     * @deprecated Use {@link #CompoundLocation(QuestPackage, String)} instead
     */
    @Deprecated
    public CompoundLocation(final String packName, final String data) throws InstructionParseException {
        this(Config.getPackages().get(packName), data);
    }

    /**
     * @param profile the {@link Profile} that should be used to resolve the {@link Variable}s
     * @return the location represented by this object
     * @throws QuestRuntimeException Is thrown when the player cannot be accessed or the resolved location is in
     *                               the wrong format.
     */
    public Location getLocation(@Nullable final Profile profile) throws QuestRuntimeException {
        final Location loc = locationData.get(profile);
        final Vector vec = vectorData == null ? new Vector() : vectorData.get(profile);
        return loc.clone().add(vec);
    }

    public LocationData getLocationData() {
        return locationData;
    }

    @Nullable
    public VectorData getVectorData() {
        return vectorData;
    }
}
