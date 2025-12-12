package org.betonquest.betonquest.api.text;

import org.betonquest.betonquest.api.QuestException;

/**
 * A decider for choosing a parser for a text.
 */
@FunctionalInterface
public interface TextParserDecider {

    /**
     * Chooses a parser for a text.
     *
     * @param text the text content to choose a parser for
     * @return the result of choosing a parser
     * @throws QuestException if an error occurs while choosing a parser
     */
    Result chooseParser(String text) throws QuestException;

    /**
     * The result of choosing a parser for a text.
     *
     * @param parserId the ID of the parser to use
     * @param text     the text content
     */
    record Result(String parserId, String text) {

    }
}
