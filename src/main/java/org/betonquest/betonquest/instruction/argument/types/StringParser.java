package org.betonquest.betonquest.instruction.argument.types;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;

/**
 * Parses a string to a string.
 */
public class StringParser implements Argument<String> {

    /**
     * Creates a new parser for strings.
     */
    public StringParser() {
    }

    @Override
    public String apply(final String string) throws QuestException {
        return string;
    }
}
