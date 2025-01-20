package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to parse values with {@link Argument}s.
 */
public interface ArgumentParser extends Parser {

    /**
     * Parses {@link #get(String, Argument)} with {@link #next()}.
     *
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the created value
     * @throws QuestException when there is no part left or the value can't be created
     */
    default <T> T get(final Argument<T> argument) throws QuestException {
        return get(next(), argument);
    }

    /**
     * Parses the string with the converter.
     *
     * @param string   the string to convert
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the created value or null if no string was provided
     * @throws QuestException when the value can't be created
     */
    @Contract("!null, _ -> !null")
    @Nullable
    default <T> T get(@Nullable final String string, final Argument<T> argument) throws QuestException {
        if (string == null) {
            return null;
        }
        return argument.apply(string);
    }

    /**
     * Parses {@link #get(String, VariableArgument)} with {@link #next()}.
     *
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the created value
     * @throws QuestException when there is no part left or the value can't be created
     */
    default <T> T get(final VariableArgument<T> argument) throws QuestException {
        return get(next(), argument);
    }

    /**
     * Parses the string with the converter.
     *
     * @param string   the string to convert
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the created value or null if no string was provided
     * @throws QuestException when the value can't be created
     */
    @Contract("!null, _ -> !null")
    @Nullable
    <T> T get(@Nullable String string, VariableArgument<T> argument) throws QuestException;
}
