package org.betonquest.betonquest.api.message;

import org.jetbrains.annotations.Nullable;

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
    void registerParser(String name, MessageParser parser);

    /**
     * Get a parser by name.
     *
     * @param name the name of the parser
     * @return the parser, or null if no parser with the given name is registered
     */
    @Nullable
    MessageParser getParser(String name);
}
