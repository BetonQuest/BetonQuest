package org.betonquest.betonquest.text.parser;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.text.TextParser;

/**
 * A parser that uses mine down to parse text.
 */
public class MineDownParser implements TextParser {

    /**
     * Constructs a new minedown text parser.
     */
    public MineDownParser() {
    }

    @Override
    public Component parse(final String text) {
        return new MineDown(text.replaceAll("(?<!\\\\)\\\\n", "\n")).toComponent();
    }
}
