package org.betonquest.betonquest.instruction.argument.types;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;

/**
 * Parses a string to a component using a message parser.
 */
public class MessageParserToComponentParser implements Argument<Component> {
    /**
     * The message parser to use.
     */
    private final MessageParser messageParser;

    /**
     * Creates a new parser for components parsed with the given message parser.
     *
     * @param messageParser the message parser to use
     */
    public MessageParserToComponentParser(final MessageParser messageParser) {
        this.messageParser = messageParser;
    }

    @Override
    public Component apply(final String string) throws QuestException {
        return messageParser.parse(string);
    }
}
