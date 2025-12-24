package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

/**
 * Parses a string to a boolean.
 */
public class BooleanParser implements SimpleArgumentParser<Boolean> {

    /**
     * The string representation of a boolean true.
     */
    private static final String TRUE = "true";

    /**
     * The string representation of a boolean false.
     */
    private static final String FALSE = "false";

    /**
     * Creates a new parser for booleans.
     */
    public BooleanParser() {
    }

    @Override
    public Boolean apply(final String value) throws QuestException {
        if (TRUE.equalsIgnoreCase(value)) {
            return true;
        } else if (FALSE.equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new QuestException("Could not parse value to boolean: " + value);
        }
    }
}
