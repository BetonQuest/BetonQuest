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

    /**
     * Parses the given value to a namespaced key.
     *
     * @param string the value to parse
     * @return the parsed key
     * @throws QuestException if the value could not be parsed
     */
    public static NamespacedKey parse(final String string) throws QuestException {
        final NamespacedKey key = NamespacedKey.fromString(string);
        if (key == null) {
            throw new QuestException("Invalid NamespacedKey '%s'!".formatted(string));
        }
        return key;
    }

    @Override
    public NamespacedKey apply(final String string) throws QuestException {
        return parse(string);
    }
}
