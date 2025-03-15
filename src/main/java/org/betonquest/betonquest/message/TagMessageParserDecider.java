package org.betonquest.betonquest.message;

import org.betonquest.betonquest.api.message.MessageParserDecider;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A decider that chooses a parser based on the message's tag.
 */
public class TagMessageParserDecider implements MessageParserDecider {
    /**
     * The pattern to match a tag in a message.
     */
    private static final Pattern TAG_PATTERN = Pattern.compile("(@\\[(?<parser>[a-zA-Z]+?)])?(?<message>.*)", Pattern.DOTALL);

    /**
     * The default parser to use if no tag is found.
     */
    private final String defaultParser;

    /**
     * Constructs a new tag message parser decider.
     *
     * @param defaultParser the default parser to use if no tag is found
     */
    public TagMessageParserDecider(final String defaultParser) {
        this.defaultParser = defaultParser;
    }

    @Override
    public Result chooseParser(final String message) throws QuestException {
        final Matcher matcher = TAG_PATTERN.matcher(message);
        if (matcher.matches()) {
            final String parser = matcher.group("parser");
            final String messageContent = matcher.group("message");
            return new Result(parser == null ? defaultParser : parser, messageContent);
        }
        throw new QuestException("Could not match tag in message: " + message);
    }
}
