package org.betonquest.betonquest.instruction.variable.location;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.regex.Pattern;

import static org.betonquest.betonquest.instruction.variable.location.VariableVector.REGEX_DATA;

/**
 * Represents a location that can contain variables.
 */
public class VariableLocation extends Variable<Location> {

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
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableLocation(final VariableProcessor variableProcessor, final QuestPackage pack, final String input)
            throws QuestException {
        super(variableProcessor, pack, input, VariableLocation::parse);
    }

    /**
     * Parses the given value to a location.
     *
     * @param value the value to parse
     * @return the parsed location
     * @throws QuestException if the value could not be parsed
     */
    public static Location parse(final String value) throws QuestException {
        final int index = value.indexOf("->");
        if (index == -1) {
            return parseLocation(value);
        }

        final Vector vector = VariableVector.parse(value.substring(index + 2));
        return parseLocation(value.substring(0, index)).add(vector);
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private static Location parseLocation(final String loc) throws QuestException {
        if (!PATTERN_LOCATION.matcher(loc).find()) {
            throw new QuestException("Incorrect location format '" + loc
                    + "'. A location has to be in the format 'x;y;z;world[;yaw;pitch]'");
        }
        final String[] parts = loc.split(";");

        final World world = VariableWorld.parse(parts[3]);
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
}
