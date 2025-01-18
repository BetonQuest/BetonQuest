package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for parsing {@link ID}s.
 */
public interface IDParser extends Parser {

    /**
     * Parses {@link #getID(String, IDArgument)} with {@link #next()}.
     *
     * @param argument the argument to parse the ID
     * @param <T>      the specific ID
     * @return the parsed ID
     * @throws QuestException when there is no part left or no such id
     */
    default <T extends ID> T getID(final IDArgument<T> argument) throws QuestException {
        return getID(next(), argument);
    }

    /**
     * Parses the string as ID.
     *
     * @param string   the string to parse as ID
     * @param argument the argument to parse the ID
     * @param <T>      the specific ID
     * @return the parsed ID or null if no string was provided
     * @throws QuestException when there is no such id
     */
    @Contract("!null, _ -> !null")
    @Nullable
    <T extends ID> T getID(@Nullable String string, IDArgument<T> argument) throws QuestException;

    /**
     * Parses {@link #getIDArray(String, IDArgument)} with {@link #next()}.
     *
     * @param argument the argument to parse the IDs
     * @param <T>      the specific ID
     * @return the parsed IDs
     * @throws QuestException when there is no part left or no such ids
     */
    default <T extends ID> T[] getIDArray(final IDArgument<T> argument) throws QuestException {
        return getIDArray(next(), argument);
    }

    /**
     * Parses IDs from the given string.
     *
     * @param string   the string to parse as IDs
     * @param argument the argument to parse the IDs
     * @param <T>      the specific ID
     * @return the parsed IDs or empty array if no string was provided
     * @throws QuestException when there is no part left
     */
    <T extends ID> T[] getIDArray(@Nullable String string, IDArgument<T> argument) throws QuestException;
}
