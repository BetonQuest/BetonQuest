package org.betonquest.betonquest.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.message.MessageParserDecider;
import org.betonquest.betonquest.api.message.MessageParserRegistry;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A message parser that decides which parser to use based on a message parser decider.
 */
public class DecidingMessageParser implements MessageParser {
    /**
     * The message parser registry to use for getting parsers.
     */
    private final MessageParserRegistry registry;

    /**
     * The message parser decider to use for deciding which parser to use.
     */
    private final MessageParserDecider decider;

    /**
     * Constructs a new deciding message parser with a message parser registry and a message parser decider.
     *
     * @param registry the message parser registry
     * @param decider  the message parser decider
     */
    public DecidingMessageParser(final MessageParserRegistry registry, final MessageParserDecider decider) {
        this.registry = registry;
        this.decider = decider;
    }

    @Override
    public Component parse(final String message) throws QuestException {
        final MessageParserDecider.Result result = decider.chooseParser(message);
        final MessageParser parser = registry.get(result.parserId());
        if (parser == null) {
            throw new QuestException("No parser found for id " + result.parserId());
        }
        return parser.parse(result.message());
    }
}
