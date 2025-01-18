package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Objectified parser for the Instruction to get a {@link T} from string.
 *
 * @param <T> what the argument returns
 */
public interface Argument<T> extends QuestFunction<String, T> {
    /**
     * Gets a {@link T} from string.
     *
     * @param string the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    @Override
    T apply(String string) throws QuestException;
}
