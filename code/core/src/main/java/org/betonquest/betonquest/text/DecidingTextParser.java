package org.betonquest.betonquest.text;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.api.text.TextParserDecider;
import org.betonquest.betonquest.api.text.TextParserRegistry;

/**
 * A text parser that decides which parser to use based on a text parser decider.
 */
public class DecidingTextParser implements TextParser {
    /**
     * The text parser registry to use for getting parsers.
     */
    private final TextParserRegistry registry;

    /**
     * The text parser decider to use for deciding which parser to use.
     */
    private final TextParserDecider decider;

    /**
     * Constructs a new deciding text parser with a text parser registry and a text parser decider.
     *
     * @param registry the text parser registry
     * @param decider  the text parser decider
     */
    public DecidingTextParser(final TextParserRegistry registry, final TextParserDecider decider) {
        this.registry = registry;
        this.decider = decider;
    }

    @Override
    public Component parse(final String text) throws QuestException {
        final TextParserDecider.Result result = decider.chooseParser(text);
        final TextParser parser = registry.get(result.parserId());
        return parser.parse(result.text());
    }
}
