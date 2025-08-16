package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.instruction.ValueChecker;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface for parsing {@link PackageArgument}s.
 */
public interface PackageArgumentConverter extends Parser {
    /**
     * Parses the {@link #next()} string with a converter.
     *
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the created value
     * @throws QuestException when there is no part left or the value can't be created
     */
    default <T> Variable<T> get(final PackageArgument<T> argument) throws QuestException {
        return get(next(), argument);
    }

    /**
     * Parses the string with a converter.
     *
     * @param string   the string to convert
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the created value or null if no string was provided
     * @throws QuestException when the value can't be created
     */
    @Contract("!null, _ -> !null")
    @Nullable
    default <T> Variable<T> get(@Nullable final String string, final PackageArgument<T> argument) throws QuestException {
        return get(string, argument, null);
    }

    /**
     * Parses the string with a converter and use a default value otherwise.
     *
     * @param string       the string to convert
     * @param argument     the converter creating the value
     * @param defaultValue the default value to use when the string is null
     * @param <T>          the value to create
     * @return the created value or null if no string was provided
     * @throws QuestException when the value can't be created
     */
    @Contract("!null, _, _ -> !null; _, _, !null -> !null")
    @Nullable
    <T> Variable<T> get(@Nullable String string, PackageArgument<T> argument, @Nullable T defaultValue) throws QuestException;

    /**
     * Parses the string provided by a key with a converter.
     *
     * @param key      the key of the value to convert
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the created value or null if there is no value for the key
     * @throws QuestException when the value can't be created
     */
    @Nullable
    default <T> Variable<T> getValue(final String key, final PackageArgument<T> argument) throws QuestException {
        return getValue(key, argument, null);
    }

    /**
     * Parses the string provided by a key with a converter and use a default value otherwise.
     *
     * @param key          the key of the value to convert
     * @param argument     the converter creating the value
     * @param defaultValue the default value to use when there is no value for the key
     * @param <T>          the value to create
     * @return the created value or null if there is no value for the key
     * @throws QuestException when the value can't be created
     */
    @Contract("_, _, !null -> !null")
    @Nullable
    default <T> Variable<T> getValue(final String key, final PackageArgument<T> argument, @Nullable final T defaultValue) throws QuestException {
        return get(getValue(key), argument, defaultValue);
    }

    /**
     * Parses the {@link #next()} string with a converter to a list.
     *
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the list of values created or an empty list if the string was null or empty
     * @throws QuestException when there is no part left or the value can't be created
     */
    default <T> Variable<List<T>> getList(final PackageArgument<T> argument) throws QuestException {
        return getList(next(), argument);
    }

    /**
     * Parses the {@link #next()} string with a converter to a list.
     *
     * @param argument     the converter creating the value
     * @param valueChecker the checker to verify valid lists
     * @param <T>          the value to create
     * @return the list of values created or an empty list if the value for the key was null or empty
     * @throws QuestException when there is no part left or the value can't be created
     */
    default <T> Variable<List<T>> getList(final PackageArgument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        return getList(next(), argument, valueChecker);
    }

    /**
     * Parses the string with a converter to a list.
     *
     * @param string   the string to convert
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the list of values created or an empty list if the string was null or empty
     * @throws QuestException when the value can't be created
     */
    default <T> Variable<List<T>> getList(@Nullable final String string, final PackageArgument<T> argument) throws QuestException {
        return getList(string, argument, (value) -> {
        });
    }

    /**
     * Parses the string with a converter to a list.
     *
     * @param string       the string to convert
     * @param argument     the converter creating the value
     * @param valueChecker the checker to verify valid lists
     * @param <T>          the value to create
     * @return the list of values created or an empty list if the string was null or empty
     * @throws QuestException when the value can't be created
     */
    <T> Variable<List<T>> getList(@Nullable String string, PackageArgument<T> argument, ValueChecker<List<T>> valueChecker) throws QuestException;

    /**
     * Parses the string provided by a key with a converter to a list.
     *
     * @param key      the key of the value to convert
     * @param argument the converter creating the value
     * @param <T>      the value to create
     * @return the list of values created or an empty list if the string was null or empty
     * @throws QuestException when the value can't be created
     */
    default <T> Variable<List<T>> getValueList(final String key, final PackageArgument<T> argument) throws QuestException {
        return getList(getValue(key), argument);
    }

    /**
     * Parses the string provided by a key with a converter to a list.
     *
     * @param key          the key of the value to convert
     * @param argument     the converter creating the value
     * @param valueChecker the checker to verify valid lists
     * @param <T>          the value to create
     * @return the list of values created or an empty list if the string was null or empty
     * @throws QuestException when the value can't be created
     */
    default <T> Variable<List<T>> getValueList(final String key, final PackageArgument<T> argument, final ValueChecker<List<T>> valueChecker) throws QuestException {
        return getList(getValue(key), argument, valueChecker);
    }
}
