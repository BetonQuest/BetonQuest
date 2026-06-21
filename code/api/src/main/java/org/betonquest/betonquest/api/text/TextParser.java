package org.betonquest.betonquest.api.text;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.jetbrains.annotations.Contract;

/**
 * A parser can convert a plain string into formatted components.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface TextParser {

    /**
     * Parse a text into a component.
     *
     * @param text the text to parse
     * @return the parsed component
     * @throws QuestException if the text could not be parsed
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null -> new")
    Component parse(String text) throws QuestException;
}
