package org.betonquest.betonquest.text.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.text.TextParser;

/**
 * A parser that uses MiniMessage to parse text.
 */
public class MiniMessageParser implements TextParser {

    /**
     * The MiniMessage instance to use for parsing.
     */
    private final MiniMessage miniMessage;

    /**
     * Constructs a new MiniMessage parser.
     *
     * @param miniMessage the MiniMessage instance to use for parsing
     */
    public MiniMessageParser(final MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public Component parse(final String text) {
        return miniMessage.deserialize(text);
    }
}
