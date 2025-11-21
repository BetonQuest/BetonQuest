package org.betonquest.betonquest.api.text;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A parser can convert a plain string into formatted components.
 */
@FunctionalInterface
public interface TextParser {
    /**
     * Parse a text into a component.
     *
     * @param text the text to parse
     * @return the parsed component
     * @throws QuestException if the text could not be parsed
     */
    Component parse(String text) throws QuestException;
}
