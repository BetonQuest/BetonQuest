package pl.betoncraft.betonquest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses various location strings with or without variables.
 */
public class LocationData {

    /**
     * This regex matches numbers like '123', '123.456', '0.456' and '.456'.
     */
    public final static String REGEX_DOUBLE = "[+-]?([0-9]*[.])?[0-9]+";
    /**
     * This regex matches matches the format of a location.
     */
    public final static String REGEX_LOCATION = REGEX_DOUBLE + ";" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + ";"
            + "((\\w|-)+\\.?)+" + "(;" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + ")?";

    /**
     * This regex matches the format of a vector.
     */
    public final static String REGEX_VECTOR = "\\(" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + ";" + REGEX_DOUBLE + "\\)";

    /**
     * This regex matches a variable with 1-n parameters.
     */
    public final static String REGEX_VARIABLE = "%(.*?)%";

    /**
     * The compiled Pattern of REGEX_LOCATION.
     */
    public final static Pattern PATTERN_LOCATION = Pattern.compile("^" + REGEX_LOCATION + "$");

    /**
     * The compiled Pattern of REGEX_VECTOR.
     */
    public final static Pattern PATTERN_VECTOR = Pattern.compile("^" + REGEX_VECTOR + "$");

    /**
     * The compiled Pattern of REGEX_VARIABLE.
     */
    private final static Pattern PATTERN_VARIABLE = Pattern.compile(REGEX_VARIABLE);

    /**
     * This location is used if the input string does not contain any variables.
     */
    private final Location location;

    /**
     * A list of all variables in the formatted location string.
     * This attribute is only used when variables exist in the input string.
     */
    private final List<Variable> locationVariables;

    /**
     * A formatted location string in which all variables have been replaced with string formatting specifiers.
     * This attribute is only used when variables exist in the input string.
     */
    private final String locationFormatted;

    /**
     * This vector is used if the input string does not contain any variables.
     */
    private final Vector vector;

    /**
     * A list of all variables in the formatted vector string.
     * This attribute is only used when variables exist in the input string.
     */
    private final List<Variable> vectorVariables;

    /**
     * A formatted vector string in which all variables have been replaced with string formatting specifiers.
     * This attribute is only used when variables exist in the input string.
     */
    private final String vectorFormatted;

     /**
     * This class parses a string into a {@link Location}. The input string has
     * to be in the format 'x;y;z;world[;yaw;pitch][-> (x;y;z)]'. All elements in square brackets are optional.
     * The last optional part is a {@link Vector} that will be added to the {@link Location} if specified.
     * Each part of the input string can be a variable instead of an {@link Integer} or {@link String}.
     *
     * @param packName     Name of the package - required for variable resolution
     * @param locationData string containing raw location in the defined format
     * @throws InstructionParseException Is thrown when an error appears while parsing the
     *                                   locationData
     */
    public LocationData(final String packName, final String locationData) throws InstructionParseException {
        String baseLocation;
        String baseVector;

        if (locationData.contains("->")) {
            final String[] parts = locationData.split("->");
            baseLocation = parts[0];
            baseVector = parts[1];
        } else {
            baseLocation = locationData;
            baseVector = null;
        }

        // TODO Remove this code in the version 1.13 or later
        // This support the old implementation of %player% as a location variable
        // Don't forget to remove the Method
        baseLocation = resolvePlayerVariable(packName, baseLocation);

        final Matcher locationMatcher = PATTERN_VARIABLE.matcher(baseLocation);
        if (locationMatcher.find()) {
            locationVariables = new ArrayList<>();
            int index = 1;
            final StringBuffer stringBuffer = new StringBuffer(baseLocation.length());
            do {
                final String variable = locationMatcher.group(0);
                final Variable var = BetonQuest.createVariable(Config.getPackages().get(packName), variable);
                locationVariables.add(var);
                final String replacement = "%" + index++ + "$s";
                locationMatcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(replacement));
            } while (locationMatcher.find());
            locationMatcher.appendTail(stringBuffer);
            location = null;
            locationFormatted = stringBuffer.toString();
        } else {
            try {
                location = parseLocation(baseLocation);
            } catch (InstructionParseException e) {
                throw new InstructionParseException("Error while parsing location. " + e.getMessage(), e);
            }
            locationVariables = null;
            locationFormatted = null;
        }

        if (baseVector == null) {
            vector = new Vector(0, 0, 0);
            vectorVariables = null;
            vectorFormatted = null;
        } else {
            final Matcher vectorMatcher = PATTERN_VARIABLE.matcher(baseVector);
            if (vectorMatcher.find()) {
                vectorVariables = new ArrayList<>();
                int index = 1;
                final StringBuffer stringBuffer = new StringBuffer(baseVector.length());
                do {
                    final String variable = vectorMatcher.group(0);
                    final Variable var = BetonQuest.createVariable(Config.getPackages().get(packName), variable);
                    vectorVariables.add(var);
                    final String replacement = "%" + index++ + "$s";
                    vectorMatcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(replacement));
                } while (vectorMatcher.find());
                vectorMatcher.appendTail(stringBuffer);
                vector = null;
                vectorFormatted = stringBuffer.toString();
            } else {
                try {
                    vector = parseVector(baseVector);
                } catch (InstructionParseException e) {
                    throw new InstructionParseException("Error while parsing vector. " + e.getMessage(), e);
                }
                vectorVariables = null;
                vectorFormatted = null;
            }
        }
    }

    @Deprecated
    private String resolvePlayerVariable(final String pack, final String strg) {
        if ("player".equalsIgnoreCase(strg)) {
            LogUtils.getLogger().log(Level.WARNING, "You still use 'player' instead of '%location%' in package '" + pack
                    + "'. This is deprecated and should be updated. This will be removed in a future version.");
            return "%location%";
        }
        return strg;
    }

    /**
     * @param playerID ID of the player - needed for location resolution
     * @return the location represented by this object
     * @throws QuestRuntimeException Is thrown when the location is defined for the player but the
     *                               player cannot be accessed.
     */
    public Location getLocation(final String playerID) throws QuestRuntimeException {
        if (playerID == null && (location == null || vector == null)) {
            throw new QuestRuntimeException("Variable location cannot accessed without the player."
                    + " consider changing it to absolute coordinates");
        }
        final Location loc = location == null ? parseVariableLocation(playerID) : location;
        final Vector vec = vector == null ? parseVariableVector(playerID) : vector;

        return loc.clone().add(vec);
    }

    private Location parseVariableLocation(final String playerID) throws QuestRuntimeException {
        String[] variables = new String[locationVariables.size()];
        for (int i = 0; i < locationVariables.size(); i++) {
            final Variable var = locationVariables.get(i);
            variables[i] = var.getValue(playerID);
        }
        final String result = String.format(locationFormatted, (Object[]) variables);
        try {
            return parseLocation(result);
        } catch (InstructionParseException e) {
            throw new QuestRuntimeException("Error while parsing location. " + e.getMessage(), e);
        }
    }

    private Vector parseVariableVector(final String playerID) throws QuestRuntimeException {
        String[] variables = new String[vectorVariables.size()];
        for (int i = 0; i < vectorVariables.size(); i++) {
            final Variable var = vectorVariables.get(i);
            variables[i] = var.getValue(playerID);
        }
        final String result = String.format(vectorFormatted, (Object[]) variables);
        try {
            return parseVector(result);
        } catch (InstructionParseException e) {
            throw new QuestRuntimeException("Error while parsing vector. " + e.getMessage(), e);
        }
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
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse a number in the location. " + e.getMessage(), e);
        }
        return new Location(world, locX, locY, locZ, yaw, pitch);
    }

    /**
     * Parses a string into a vector. The Vector has to be in the format
     * '(x;y;z)'.
     *
     * @param vector The string that represents the vector
     * @return The vector
     * @throws InstructionParseException Is thrown when the Vector is not in the right format or if
     *                                   the values couldn't be parsed into double values.
     */
    public static Vector parseVector(final String vector) throws InstructionParseException {
        if (vector == null || !PATTERN_VECTOR.matcher(vector).find()) {
            throw new InstructionParseException(
                    "Incorrect vector format '" + vector + "'. A vector has to be in the format '(x;y;z)'");
        }
        final String[] parts = vector.substring(1, vector.indexOf(')')).split(";");
        final double locX;
        final double locY;
        final double locZ;
        try {
            locX = Double.parseDouble(parts[0]);
            locY = Double.parseDouble(parts[1]);
            locZ = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse a number in the vector. " + e.getMessage(), e);
        }
        return new Vector(locX, locY, locZ);
    }
}
