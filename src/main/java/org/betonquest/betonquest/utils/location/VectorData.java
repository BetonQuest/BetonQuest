package org.betonquest.betonquest.utils.location;


import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.util.Vector;

import java.util.regex.Pattern;

/**
 * This class parses various vector strings with or without {@link Variable}s.
 */
public class VectorData extends AbstractData<Vector> {
    /**
     * This regex matches the format of a vector.
     */
    public final static String REGEX_VECTOR = "\\(" + REGEX_DATA + ";" + REGEX_DATA + ";" + REGEX_DATA + "\\)";

    /**
     * The compiled Pattern of {@link VectorData#REGEX_VECTOR}.
     */
    public final static Pattern PATTERN_VECTOR = Pattern.compile("^" + REGEX_VECTOR + "$");

    /**
     * This class parses a string into a {@link Vector}.
     * The input string has to be in the format '(x;y;z)'.
     * Each part of the input string can be a {@link Variable}s instead of an {@link Integer} or {@link String}.
     *
     * @param packName Name of the {@link ConfigPackage} - required for {@link Variable} resolution
     * @param data     string containing raw {@link Vector} in the defined format
     * @throws InstructionParseException Is thrown when an error appears while parsing the {@link Variable}s or {@link Vector}
     */
    public VectorData(final String packName, final String data) throws InstructionParseException {
        super(packName, data);
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
        try {
            final double locX = Double.parseDouble(parts[0]);
            final double locY = Double.parseDouble(parts[1]);
            final double locZ = Double.parseDouble(parts[2]);
            return new Vector(locX, locY, locZ);
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse a number in the vector. " + e.getMessage(), e);
        }
    }

    @Override
    public Vector parse(final String objectString) throws InstructionParseException {
        return VectorData.parseVector(objectString);
    }

    @Override
    protected Vector clone(final Vector object) {
        return object.clone();
    }
}
