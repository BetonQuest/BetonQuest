package org.betonquest.betonquest.api.instruction.argument.types;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.Argument;

import java.util.UUID;

/**
 * Parses a string to a UUID.
 */
public class UUIDParser implements Argument<UUID> {

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
