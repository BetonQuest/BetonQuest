package org.betonquest.betonquest.message.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.message.MessageParser;

/**
 * A parser that uses legacy formatting codes to parse messages.
 */
public class LegacyParser implements MessageParser {

    /**
     * Constructs a new legacy parser for parsing messages.
     */
    public LegacyParser() {
    }

    @Override
    public Component parse(final String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}
