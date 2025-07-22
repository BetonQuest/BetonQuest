package org.betonquest.betonquest.message.parser;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.message.MessageParser;

/**
 * A parser that uses mine down to parse messages.
 */
public class MineDownMessageParser implements MessageParser {

    /**
     * Constructs a new minedown message parser.
     */
    public MineDownMessageParser() {
    }

    @Override
    public Component parse(final String message) {
        return new MineDown(message.replaceAll("(?<!\\\\)\\\\n", "\n")).toComponent();
    }
}
