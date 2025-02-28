package org.betonquest.betonquest.message.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.message.MessageParser;

/**
 * A parser that uses mini message to parse messages.
 */
public class MiniMessageParser implements MessageParser {
    /**
     * The mini message instance to use for parsing.
     */
    private final MiniMessage miniMessage;

    /**
     * Constructs a new mini message parser.
     *
     * @param miniMessage the mini message instance to use for parsing
     */
    public MiniMessageParser(final MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public Component parse(final String message) {
        return miniMessage.deserialize(message);
    }
}
