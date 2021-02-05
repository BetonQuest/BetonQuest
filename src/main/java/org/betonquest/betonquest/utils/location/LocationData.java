package org.betonquest.betonquest.utils.location;


import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.regex.Pattern;

/**
 * This class parses various location strings with or without {@link Variable}s.
 */
public class LocationData extends AbstractData<Location> {
    /**
     * This regex matches the format of a location.
     */
    public final static String REGEX_LOCATION = REGEX_DATA + ";" + REGEX_DATA + ";" + REGEX_DATA + ";"
            + REGEX_DATA + "(;" + REGEX_DATA + ";" + REGEX_DATA + ")?";

    /**
     * The compiled Pattern of {@link LocationData#REGEX_LOCATION}.
     */
    public final static Pattern PATTERN_LOCATION = Pattern.compile("^" + REGEX_LOCATION + "$");

    /**
     * This class parses a string into a {@link Location}.
     * The input string has to be in the format 'x;y;z;world[;yaw;pitch]'. All elements in square brackets are optional.
     * Each part of the input string can be a {@link Variable}s instead of an {@link Integer} or {@link String}.
     *
     * @param packName Name of the {@link ConfigPackage} - required for {@link Variable} resolution
     * @param data     string containing raw {@link Location} in the defined format
     * @throws InstructionParseException Is thrown when an error appears while parsing the {@link Variable}s or
     *                                   {@link Location}
     */
    public LocationData(final String packName, final String data) throws InstructionParseException {
        super(packName, data);
    }

    /**
     * Parses a string into a location. The location has to be in the format
     * '(x;y;z;world[;yaw;pitch])'.
     *
     * @param loc The string that represents the location
     * @return The location
     * @throws InstructionParseException Is thrown when the location is not in the right format or if it
     *                                   couldn't be parsed into double values or the World does not
     *                                   exist.
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public static Location parseLocation(final String loc) throws InstructionParseException {
        if (loc == null || !PATTERN_LOCATION.matcher(loc).find()) {
            throw new InstructionParseException("Incorrect location format '" + loc
                    + "'. A location has to be in the format 'x;y;z;world[;yaw;pitch]'");
        }
        final String[] parts = loc.split(";");

        final World world = Bukkit.getWorld(parts[3]);
        if (world == null) {
            throw new InstructionParseException("World " + parts[3] + " does not exists.");
        }
        final double locX;
        final double locY;
        final double locZ;
        float yaw = 0;
        float pitch = 0;
        try {
            locX = Double.parseDouble(parts[0]);
            locY = Double.parseDouble(parts[1]);
            locZ = Double.parseDouble(parts[2]);
            if (parts.length == 6) {
                yaw = Float.parseFloat(parts[4]);
                pitch = Float.parseFloat(parts[5]);
            }
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse a number in the location. " + e.getMessage(), e);
        }
        return new Location(world, locX, locY, locZ, yaw, pitch);
    }

    @Override
    public Location parse(final String objectString) throws InstructionParseException {
        return LocationData.parseLocation(objectString);
    }

    @Override
    protected Location clone(final Location object) {
        return object.clone();
    }
}
