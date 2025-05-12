package org.betonquest.betonquest.instruction.argument.types.location;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses a string to a vector.
 */
public class VectorParser implements Argument<Vector> {
    /**
     * This regex matches everything except ';'.
     */
    protected static final String REGEX_DATA = "[^;]+";

    /**
     * This regex matches the format of a vector.
     */
    private static final String REGEX_VECTOR = "\\(" + REGEX_DATA + ";" + REGEX_DATA + ";" + REGEX_DATA + "\\)";

    /**
     * The compiled Pattern of {@link #REGEX_VECTOR}.
     */
    private static final Pattern PATTERN_VECTOR = Pattern.compile("^" + REGEX_VECTOR + "$");

    /**
     * Creates a new parser for vectors.
     */
    public VectorParser() {
    }

    /**
     * Parses the given value to a vector.
     * The value can be a single vector or a chain of vectors.
     *
     * @param value the value to parse
     * @return the parsed vector
     * @throws QuestException if the value could not be parsed
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public static Vector parse(final String value) throws QuestException {
        final String[] parts = value.split("->");
        if (parts.length == 1) {
            return parseVector(parts[0]);
        }

        final List<Vector> vectors = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            vectors.add(parseVector(parts[i]));
        }
        return vectors.stream().reduce(parseVector(parts[0]), Vector::add);
    }

    private static Vector parseVector(final String vector) throws QuestException {
        if (!PATTERN_VECTOR.matcher(vector).find()) {
            throw new QuestException(
                    "Incorrect vector format '" + vector + "'. A vector has to be in the format '(x;y;z)'");
        }
        final String[] parts = vector.substring(1, vector.indexOf(')')).split(";");
        try {
            final double locX = Double.parseDouble(parts[0]);
            final double locY = Double.parseDouble(parts[1]);
            final double locZ = Double.parseDouble(parts[2]);
            return new Vector(locX, locY, locZ);
        } catch (final NumberFormatException e) {
            throw new QuestException("Could not parse a number in the vector. " + e.getMessage(), e);
        }
    }

    @Override
    public Vector apply(final String string) throws QuestException {
        return parse(string);
    }

    @Override
    public Vector clone(final Vector value) {
        return value.clone();
    }
}
