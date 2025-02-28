package org.betonquest.betonquest.api.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A parser can convert a plain string into formatted components.
 */
public interface MessageParser {
    /**
     * Parse a message into a component.
     *
     * @param message The message to parse.
     * @return The parsed component.
     * @throws QuestException If the message could not be parsed.
     */
    Component parse(String message) throws QuestException;
}
