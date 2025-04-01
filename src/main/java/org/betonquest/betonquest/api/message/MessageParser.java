package org.betonquest.betonquest.api.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A parser can convert a plain string into formatted components.
 */
@FunctionalInterface
public interface MessageParser {
    /**
     * Parse a message into a component.
     *
     * @param message the message to parse
     * @return the parsed component
     * @throws QuestException if the message could not be parsed
     */
    Component parse(String message) throws QuestException;
}
