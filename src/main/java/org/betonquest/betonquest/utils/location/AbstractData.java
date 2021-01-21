package org.betonquest.betonquest.utils.location;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a abstract implementation to pars various strings with or without {@link Variable}s.
 */
abstract class AbstractData<T extends Cloneable> {
    /**
     * This regex matches everything except ';'.
     */
    public final static String REGEX_DATA = "[^;]+";

    /**
     * This regex matches a variable with 1-n parameters.
     */
    public final static String REGEX_VARIABLE = "%(.*?)%";

    /**
     * The compiled Pattern of REGEX_VARIABLE.
     */
    private final static Pattern PATTERN_VARIABLE = Pattern.compile(REGEX_VARIABLE);

    /**
     * This object is used if the input string does not contain any {@link Variable}s.
     */
    private final T object;

    /**
     * A list of all {@link Variable}s in the formatted object string.
     * This attribute is only used when {@link Variable}s exist in the input string.
     */
    private final List<Variable> objectVariables;

    /**
     * A formatted object string in which all {@link Variable}s have been replaced with string formatting specifiers.
     * This attribute is only used when {@link Variable}s exist in the input string.
     */
    private final String objectFormatted;

    /**
     * This class parses a string into a object.
     * Each part of the input string can be a {@link Variable}s instead of an {@link Integer} or {@link String}.
     *
     * @param packName Name of the {@link pl.betoncraft.betonquest.config.ConfigPackage} - required for
     *                 {@link Variable} resolution
     * @param data     string containing raw object in the defined format
     * @throws InstructionParseException Is thrown when an error appears while parsing the {@link Variable}s or the object
     */
    public AbstractData(final String packName, final String data) throws InstructionParseException {
        final Matcher variableMatcher = PATTERN_VARIABLE.matcher(data);
        if (variableMatcher.find()) {
            objectVariables = new ArrayList<>();
            int index = 1;
            final StringBuffer stringBuffer = new StringBuffer(data.length());
            do {
                final String variable = variableMatcher.group(0);
                final Variable var = BetonQuest.createVariable(Config.getPackages().get(packName), variable);
                objectVariables.add(var);
                final String replacement = "%" + index++ + "$s";
                variableMatcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(replacement));
            } while (variableMatcher.find());
            variableMatcher.appendTail(stringBuffer);
            objectFormatted = stringBuffer.toString();
            object = null;
        } else {
            try {
                object = parse(data);
            } catch (final InstructionParseException e) {
                throw new InstructionParseException("Error while parsing: " + e.getMessage(), e);
            }
            objectVariables = null;
            objectFormatted = null;
        }
    }

    /**
     * Parses a string with resolved {@link Variable}s into a object.
     *
     * @param objectString The string that represents the vector
     * @return The parsed object
     * @throws InstructionParseException Is thrown when the objectString is not in the right format or if
     *                                   the values couldn't be parsed.
     */
    public abstract T parse(final String objectString) throws InstructionParseException;

    /**
     * Clones the object to prevent illegal modification.
     *
     * @param object The object
     * @return The cloned object
     */
    protected abstract T clone(final T object);

    /**
     * Gets the object
     *
     * @param playerID ID of the player - needed for {@link Variable}s resolution
     * @return The object represented by this object
     * @throws QuestRuntimeException Is thrown when the objectString is not in the right format or if
     *                               the values couldn't be parsed.
     */
    public T get(final String playerID) throws QuestRuntimeException {
        return object == null ? parseVariableObject(playerID) : clone(object);
    }

    private T parseVariableObject(final String playerID) throws QuestRuntimeException {
        if (playerID == null) {
            throw new QuestRuntimeException("Variable cannot be accessed without the player."
                    + " Consider changing it to absolute coordinates");
        }
        final String[] variables = new String[this.objectVariables.size()];
        for (int i = 0; i < this.objectVariables.size(); i++) {
            final Variable var = this.objectVariables.get(i);
            variables[i] = var.getValue(playerID);
        }
        final String result = String.format(objectFormatted, (Object[]) variables);
        try {
            return parse(result);
        } catch (final InstructionParseException e) {
            throw new QuestRuntimeException("Error while parsing: " + e.getMessage(), e);
        }
    }
}
