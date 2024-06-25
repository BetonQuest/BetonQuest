package org.betonquest.betonquest.instruction.variable.location;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.regex.Pattern;

import static org.betonquest.betonquest.instruction.variable.location.VariableVector.REGEX_DATA;

/**
 * This class represents a location variable.
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
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    public VariableLocation(final VariableProcessor variableProcessor, final QuestPackage pack, final String input)
            throws InstructionParseException {
        super(variableProcessor, pack, input, VariableLocation::parse);
    }

    private static Location parse(final String value) throws QuestRuntimeException {
        final int index = value.indexOf("->");
        if (index == -1) {
            return parseLocation(value);
        }

        final Vector vector = VariableVector.parse(value.substring(index + 2));
        return parseLocation(value.substring(0, index)).add(vector);
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private static Location parseLocation(final String loc) throws QuestRuntimeException {
        if (!PATTERN_LOCATION.matcher(loc).find()) {
            throw new QuestRuntimeException("Incorrect location format '" + loc
                    + "'. A location has to be in the format 'x;y;z;world[;yaw;pitch]'");
        }
        final String[] parts = loc.split(";");

        final World world = Bukkit.getWorld(parts[3]);
        if (world == null) {
            throw new QuestRuntimeException("World " + parts[3] + " does not exists.");
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
            throw new QuestRuntimeException("Could not parse a number in the location. " + e.getMessage(), e);
        }
        return new Location(world, locX, locY, locZ, yaw, pitch);
    }

    /**
     * Get the location value of the variable.
     *
     * @param profile the profile to get the value for
     * @return the location value of the variable
     * @throws QuestRuntimeException if the location could not be resolved
     * @deprecated use {@link #getValue(Profile)}} instead
     */
    @Deprecated
    public Location getLocation(final Profile profile) throws QuestRuntimeException {
        return getValue(profile);
    }
}
