package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

import java.util.UUID;

/**
 * Parses a string to a UUID.
 */
public class UUIDParser implements SimpleArgumentParser<UUID> {

    /**
     * Creates a new parser for UUIDs.
     */
    public UUIDParser() {
    }

    @Override
    public UUID apply(final String string) throws QuestException {
        try {
            return UUID.fromString(string);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(exception);
        }
    }
}
