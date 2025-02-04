package org.betonquest.betonquest.message.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.message.MessageParser;

public class LegacyParser implements MessageParser {

    @Override
    public Component parse(final String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}
