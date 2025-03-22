package org.betonquest.betonquest.message.parser;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.message.MessageParser;

import java.util.function.Consumer;

/**
 * A parser that uses mine down to parse messages.
 */
public class MineDownMessageParser implements MessageParser {
    /**
     * The mine down options to use for parsing.
     */
    private final Consumer<MineDown>[] mineDownOptions;

    /**
     * Constructs a new minedown message parser.
     *
     * @param mineDownOptions the minedown modifications on the parsing mine down instance after initialization
     */
    public MineDownMessageParser(final Consumer<MineDown>... mineDownOptions) {
        this.mineDownOptions = mineDownOptions.clone();
    }

    @Override
    public Component parse(final String message) {
        final MineDown mineDown = new MineDown(message.replaceAll("\\\\n", "\n"));
        for (final Consumer<MineDown> consumer : mineDownOptions) {
            consumer.accept(mineDown);
        }
        return mineDown.toComponent();
    }
}
