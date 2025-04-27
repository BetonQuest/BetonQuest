package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for parsing {@link PackageArgument}s.
 */
public interface PackageParser extends Parser {

    /**
     * Parses {@link #get(String, PackageArgument)} with {@link #next()}.
     *
     * @param argument the argument to parse the ID
     * @param <T>      the specific ID
     * @return the parsed ID
     * @throws QuestException when there is no part left or no such id
     */
    default <T> Variable<T> get(final PackageArgument<T> argument) throws QuestException {
        return get(next(), argument);
    }

    /**
     * Parses the string as {@link T}.
     *
     * @param string   the string to parse as ID
     * @param argument the argument to parse the ID
     * @param <T>      the specific ID
     * @return the parsed ID or null if no string was provided
     * @throws QuestException when there is no such id
     */
    @Contract("!null, _ -> !null")
    @Nullable
    <T> Variable<T> get(@Nullable String string, PackageArgument<T> argument) throws QuestException;
}
