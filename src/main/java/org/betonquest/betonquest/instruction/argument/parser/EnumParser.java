package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for parsing {@link Enum}s.
 */
public interface EnumParser extends Parser {

    /**
     * Parses {@link #getEnum(String, Class)} with {@link #next()}.
     * <p>
     * The enum needs to be in upper case.
     *
     * @param clazz the enum class to parse
     * @param <T>   the specific enum
     * @return the parsed enum
     * @throws QuestException when there is no part left or no such enum
     */
    default <T extends Enum<T>> T getEnum(final Class<T> clazz) throws QuestException {
        return getEnum(next(), clazz);
    }

    /**
     * Parses the string as enum.
     * <p>
     * The enum needs to be in upper case.
     *
     * @param string the string to parse as enum
     * @param clazz  the enum class to parse
     * @param <T>    the specific enum
     * @return the parsed enum or null if no string was provided
     * @throws QuestException when there is no such enum
     */
    @Contract("null, _ -> null; !null, _ -> !null")
    @Nullable
    default <T extends Enum<T>> T getEnum(@Nullable final String string, final Class<T> clazz) throws QuestException {
        return getEnum(string, clazz, null);
    }

    /**
     * Parses the string as enum.
     * <p>
     * The enum needs to be in upper case.
     *
     * @param string       the string to parse as enum
     * @param clazz        the enum class to parse
     * @param defaultValue the fallback value wenn no string was provided
     * @param <T>          the specific enum
     * @return the parsed enum or null if no string or fallback was provided
     * @throws QuestException when there is no such enum
     */
    @Contract("_, _, !null -> !null")
    @Nullable
    <T extends Enum<T>> T getEnum(@Nullable String string, Class<T> clazz, @Nullable T defaultValue) throws QuestException;
}
