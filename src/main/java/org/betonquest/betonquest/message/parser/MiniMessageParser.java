package org.betonquest.betonquest.message.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.message.MessageParser;

public class MiniMessageParser implements MessageParser {
    private final MiniMessage miniMessage;

    public MiniMessageParser(final MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public Component parse(final String message) {
        return miniMessage.deserialize(message);
    }
}
