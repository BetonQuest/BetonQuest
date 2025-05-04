package org.betonquest.betonquest.api.message;

import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A registry for message parsers.
 */
public interface MessageParserRegistry {
    /**
     * Register a parser by name.
     *
     * @param name   the name of the parser
     * @param parser the parser to register
     */
    void register(String name, MessageParser parser);

    /**
     * Get a parser by name.
     *
     * @param name the name of the parser
     * @return the parser, or null if no parser with the given name is registered
     * @throws QuestException when there is no Message Parser registered under that name
     */
    MessageParser get(String name) throws QuestException;
}
