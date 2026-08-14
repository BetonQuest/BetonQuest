package org.betonquest.betonquest.lib.instruction.reader;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Defines a strategy for reading arguments from an instruction.
 *
 * @param <T> the type of the resulting argument value
 */
public interface InstructionReaderStrategy<T> {

    /**
     * Gets the next argument.
     *
     * @return the next argument
     * @throws QuestException if there are no more arguments
     */
    T getNext() throws QuestException;

    /**
     * Gets the value of an argument with the given prefix.
     *
     * @param prefix the prefix of the argument
     * @return the value of the argument, or null if not found
     * @throws QuestException if there are no more arguments
     */
    @Nullable
    T getOptional(String prefix) throws QuestException;

    /**
     * Gets the flag state and value of a flag with the given prefix.
     *
     * @param prefix the prefix of the flag
     * @return the flag state and value, or an empty string if not found
     * @throws QuestException if there are no more flags
     */
    Map.Entry<FlagState, T> getFlag(String prefix) throws QuestException;

    /**
     * Gets the named elements by a key filter.
     *
     * @param keyFilter the filter for the keys
     * @return the named elements
     * @throws QuestException if there are no more named elements
     */
    Map<String, T> getNamed(Predicate<String> keyFilter) throws QuestException;
}
