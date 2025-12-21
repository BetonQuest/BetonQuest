package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.regex.Pattern;

import static org.betonquest.betonquest.api.instruction.argument.parser.VectorParser.REGEX_DATA;

/**
 * Parses a string to a location.
 */
public class LocationParser implements Argument<Location> {

    /**
     * This regex matches the format of a location.
     */
    private static final String REGEX_LOCATION = REGEX_DATA + ";" + REGEX_DATA + ";" + REGEX_DATA + ";"
            + REGEX_DATA + "(;" + REGEX_DATA + ";" + REGEX_DATA + ")?";

    /**
     * The compiled Pattern of {@link #REGEX_LOCATION}.
     */
    private static final Pattern PATTERN_LOCATION = Pattern.compile("^" + REGEX_LOCATION + "$");

    /**
     * The server to use.
     */
    private final Server server;

    /**
     * Creates a new parser for locations.
     *
     * @param server the server to use
     */
    public LocationParser(final Server server) {
        this.server = server;
    }

    /**
     * Parses the given value to a location.
     *
     * @param value  the value to parse
     * @param server the server to use
     * @return the parsed location
     * @throws QuestException if the value could not be parsed
     */
    public static Location parse(final String value, final Server server) throws QuestException {
        final int index = value.indexOf("->");
        if (index == -1) {
            return parseLocation(value, server);
        }

        final Vector vector = VectorParser.parse(value.substring(index + 2));
        return parseLocation(value.substring(0, index), server).add(vector);
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private static Location parseLocation(final String loc, final Server server) throws QuestException {
        if (!PATTERN_LOCATION.matcher(loc).find()) {
            throw new QuestException("Incorrect location format '" + loc
                    + "'. A location has to be in the format 'x;y;z;world[;yaw;pitch]'");
        }
        final String[] parts = loc.split(";");

        final World world = WorldParser.parse(parts[3], server);
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
            throw new QuestException("Could not parse a number in the location. " + e.getMessage(), e);
        }
        return new Location(world, locX, locY, locZ, yaw, pitch);
    }

    @Override
    public Location apply(final String string) throws QuestException {
        return parse(string, server);
    }

    @Override
    public Location cloneValue(final Location value) {
        return value.clone();
    }
}
