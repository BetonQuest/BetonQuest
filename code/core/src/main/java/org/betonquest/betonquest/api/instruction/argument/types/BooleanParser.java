package org.betonquest.betonquest.api.instruction.argument.types;

import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * Parses a string to a boolean.
 */
public class BooleanParser implements Argument<Boolean> {
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
