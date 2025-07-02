package org.betonquest.betonquest.instruction.argument.types;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;

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
            return java.util.UUID.fromString(string);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(exception);
        }
    }
}
