package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

/**
 * Parses a string to a string.
 */
public class StringParser implements SimpleArgumentParser<String> {

    /**
     * Creates a new parser for strings.
     */
    public StringParser() {
    }

    @Override
    public String apply(final String string) {
        return string;
    }
}
