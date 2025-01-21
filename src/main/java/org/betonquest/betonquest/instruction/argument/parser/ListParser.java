package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface to parse collection values with {@link Argument}s.
 */
public interface ListParser extends ArgumentParser {

    /**
     * Parses {@link #getList(String, Argument)} with {@link #next()}.
     *
     * @param argument the converter creating the values
     * @param <T>      the value to create
     * @return the created values
     * @throws QuestException when there is no part left or the values can't be created
     */
    default <T> List<T> getList(final Argument<T> argument) throws QuestException {
        return getList(next(), argument);
    }

    /**
     * Parses the string with {@link #getArray(String)} and converts the parts it.
     *
     * @param string   the string to convert
     * @param argument the converter creating the values
     * @param <T>      the value to create
     * @return the created values or empty list if no string was provided
     * @throws QuestException when the values can't be created
     */
    default <T> List<T> getList(@Nullable final String string, final Argument<T> argument) throws QuestException {
        if (string == null) {
            return new ArrayList<>(0);
        }
        final String[] array = getArray(string);
        final List<T> list = new ArrayList<>(array.length);
        for (final String part : array) {
            list.add(get(part, argument));
        }
        return list;
    }

    /**
     * Parses {@link #getList(String, VariableArgument)} with {@link #next()}.
     *
     * @param argument the converter creating the values
     * @param <T>      the value to create
     * @return the created values
     * @throws QuestException when there is no part left or the values can't be created
     */
    default <T> List<T> getList(final VariableArgument<T> argument) throws QuestException {
        return getList(next(), argument);
    }

    /**
     * Parses the string with {@link #getArray(String)} and converts the parts it.
     *
     * @param string   the string to convert
     * @param argument the converter creating the values
     * @param <T>      the value to create
     * @return the created values or empty list if no string was provided
     * @throws QuestException when the values can't be created
     */
    default <T> List<T> getList(@Nullable final String string, final VariableArgument<T> argument) throws QuestException {
        if (string == null) {
            return new ArrayList<>(0);
        }
        final String[] array = getArray(string);
        final List<T> list = new ArrayList<>(array.length);
        for (final String part : array) {
            list.add(get(part, argument));
        }
        return list;
    }
}
