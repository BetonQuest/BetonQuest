package org.betonquest.betonquest.text;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.TextParserDecider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A decider that chooses a parser based on the text's tag.
 */
public class TagTextParserDecider implements TextParserDecider {
    /**
     * The pattern to match a tag in a text.
     */
    private static final Pattern TAG_PATTERN = Pattern.compile("(@\\[(?<parser>[a-zA-Z]+?)])?(?<text>.*)", Pattern.DOTALL);

    /**
     * The default parser to use if no tag is found.
     */
    private final String defaultParser;

    /**
     * Constructs a new tag text parser decider.
     *
     * @param defaultParser the default parser to use if no tag is found
     */
    public TagTextParserDecider(final String defaultParser) {
        this.defaultParser = defaultParser;
    }

    @Override
    public Result chooseParser(final String text) throws QuestException {
        final Matcher matcher = TAG_PATTERN.matcher(text);
        if (matcher.matches()) {
            final String parser = matcher.group("parser");
            final String textContent = matcher.group("text");
            return new Result(parser == null ? defaultParser : parser, textContent);
        }
        throw new QuestException("Could not match tag in text: " + text);
    }
}
