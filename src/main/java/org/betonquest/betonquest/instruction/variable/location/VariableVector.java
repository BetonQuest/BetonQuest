package org.betonquest.betonquest.instruction.variable.location;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a vector that can contain variables.
 */
public class VariableVector extends Variable<Vector> {
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
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableVector(final VariableProcessor variableProcessor, final QuestPackage pack, final String input) throws QuestException {
        super(variableProcessor, pack, input, VariableVector::parse);
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

    /**
     * Gets the value of the variable.
     *
     * @param profile the profile to get the value from
     * @return the value of the variable
     * @throws QuestException if the value could not be resolved
     * @deprecated use {@link #getValue(Profile)} instead
     */
    @Deprecated
    public Vector get(final Profile profile) throws QuestException {
        return getValue(profile);
    }
}
