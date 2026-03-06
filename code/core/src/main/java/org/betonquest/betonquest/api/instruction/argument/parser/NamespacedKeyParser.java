package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.bukkit.NamespacedKey;

/**
 * Parses a string to a NamespacedKey.
 */
public class NamespacedKeyParser implements SimpleArgumentParser<NamespacedKey> {

    /**
     * Creates a new parser for NamespacedKeys.
     */
    public NamespacedKeyParser() {
    }

    @Override
    public NamespacedKey apply(final String string) throws QuestException {
        final NamespacedKey key = NamespacedKey.fromString(string);
        if (key == null) {
            throw new QuestException("Invalid NamespacedKey '%s'!".formatted(string));
        }
        return key;
    }
}
