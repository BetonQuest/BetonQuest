package org.betonquest.betonquest.instruction.argument.parser;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Interface for the instruction to split method definitions.
 */
public interface Parser {

    /**
     * Gets the next string.
     * <p>
     * Calling this method again will result in a different result.
     *
     * @return the following string
     * @throws QuestException when there is no part left
     */
    String next() throws QuestException;

    /**
     * Gets an optional key:value instruction argument or null if the key is not present.
     *
     * @param prefix the prefix of the optional value without ":"
     * @return the value or null
     */
    @Nullable
    default String getOptional(final String prefix) {
        return getOptional(prefix, null);
    }

    /**
     * Gets an optional value or the default value if value is not present.
     *
     * @param prefix        the prefix of the optional value
     * @param defaultString the default value
     * @return the value or the default value
     */
    @Contract("_, !null -> !null")
    @Nullable
    default String getOptional(final String prefix, @Nullable final String defaultString) {
        return getOptionalArgument(prefix).orElse(defaultString);
    }

    /**
     * Gets an optional value with the given prefix.
     *
     * @param prefix the prefix of the optional value
     * @return an {@link Optional} containing the value or an empty {@link Optional} if the value is not present
     */
    Optional<String> getOptionalArgument(String prefix);

    /**
     * Gets {@link #getArray(String)} with {@link #next()}.
     *
     * @return the split string
     * @throws QuestException when there is no part left
     */
    default String[] getArray() throws QuestException {
        return getArray(next());
    }

    /**
     * Splits the string by {@code ,}.
     * <p>
     * Passing null results in an empty array.
     *
     * @param string the string to split
     * @return the split string
     */
    default String[] getArray(@Nullable final String string) {
        if (string == null) {
            return new String[0];
        }
        return StringUtils.split(string, ",");
    }
}
