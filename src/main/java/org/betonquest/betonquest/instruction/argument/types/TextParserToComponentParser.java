package org.betonquest.betonquest.instruction.argument.types;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.instruction.argument.Argument;

/**
 * Parses a string to a component using a text parser.
 */
public class TextParserToComponentParser implements Argument<Component> {
    /**
     * The text parser to use.
     */
    private final TextParser textParser;

    /**
     * Creates a new parser for components parsed with the given text parser.
     *
     * @param textParser the text parser to use
     */
    public TextParserToComponentParser(final TextParser textParser) {
        this.textParser = textParser;
    }

    @Override
    public Component apply(final String string) throws QuestException {
        return textParser.parse(string);
    }
}
