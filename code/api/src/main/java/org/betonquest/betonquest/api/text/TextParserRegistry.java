package org.betonquest.betonquest.api.text;

import org.betonquest.betonquest.api.QuestException;

/**
 * A registry for text parsers.
 */
public interface TextParserRegistry {

    /**
     * Register a parser by name.
     *
     * @param name       the name of the text
     * @param textParser the parser to register
     */
    void register(String name, TextParser textParser);

    /**
     * Get a parser by name.
     *
     * @param name the name of the parser
     * @return the parser, or null if no parser with the given name is registered
     * @throws QuestException when there is no text Parser registered under that name
     */
    TextParser get(String name) throws QuestException;
}
