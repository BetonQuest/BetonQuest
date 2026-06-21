package org.betonquest.betonquest.api.text;

import org.betonquest.betonquest.api.QuestException;
import org.jetbrains.annotations.Contract;

/**
 * A registry for text parsers.
 *
 * @since 3.0.0
 */
public interface TextParserRegistry {

    /**
     * Register a parser by name.
     *
     * @param name       the name of the text
     * @param textParser the parser to register
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    void register(String name, TextParser textParser);

    /**
     * Get a parser by name.
     *
     * @param name the name of the parser
     * @return the parser, or null if no parser with the given name is registered
     * @throws QuestException when there is no text Parser registered under that name
     * @since 3.0.0
     */
    @Contract(pure = true)
    TextParser get(String name) throws QuestException;
}
