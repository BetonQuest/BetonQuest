package org.betonquest.betonquest.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.message.MessageParserDecider;
import org.betonquest.betonquest.api.message.MessageParserRegistry;
import org.betonquest.betonquest.api.quest.QuestException;

public class DecidingMessageParser implements MessageParser {

    private final MessageParserRegistry registry;

    private final MessageParserDecider decider;

    public DecidingMessageParser(final MessageParserRegistry registry, final MessageParserDecider decider) {
        this.registry = registry;
        this.decider = decider;
    }

    @Override
    public Component parse(final String message) throws QuestException {
        final MessageParserDecider.Result result = decider.chooseParser(message);
        final MessageParser parser = registry.getParser(result.parserId());
        if (parser == null) {
            throw new QuestException("No parser found for id " + result.parserId());
        }
        return parser.parse(result.message());
    }
}
