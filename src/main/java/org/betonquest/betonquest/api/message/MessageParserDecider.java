package org.betonquest.betonquest.api.message;

import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A decider for choosing a parser for a message.
 */
@FunctionalInterface
public interface MessageParserDecider {
    /**
     * Chooses a parser for a message.
     *
     * @param message the message content to choose a parser for
     * @return the result of choosing a parser
     * @throws QuestException if an error occurs while choosing a parser
     */
    Result chooseParser(String message) throws QuestException;

    /**
     * The result of choosing a parser for a message.
     *
     * @param parserId the ID of the parser to use
     * @param message  the message content
     */
    record Result(String parserId, String message) {
    }
}
