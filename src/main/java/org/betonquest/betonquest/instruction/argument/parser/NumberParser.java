package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.jetbrains.annotations.Nullable;

/**
 * Parses fixed numbers.
 */
public interface NumberParser extends Parser {
    /**
     * Parses the provided string with the number argument.
     *
     * @param string   the string to parse
     * @param argument the argument parsing the string
     * @return the parsed number object
     * @throws QuestException when the number can't be parsed by the given argument
     */
    Number parseNumber(String string, Argument<Number> argument) throws QuestException;

    /**
     * Parses {@link #next()} as int value.
     *
     * @return the parsed int
     * @throws QuestException when there is no part left, or it can't be parsed as int
     */
    default int getInt() throws QuestException {
        return getInt(next(), 0);
    }

    /**
     * Parses the string as int.
     *
     * @param string the string to parse as int
     * @param def    the fallback to use when the string is null
     * @return parsed int or fallback
     * @throws QuestException when the string can't be parsed as int
     */
    default int getInt(@Nullable final String string, final int def) throws QuestException {
        if (string == null) {
            return def;
        }
        return parseNumber(string, Integer::parseInt).intValue();
    }

    /**
     * Parses {@link #next()} as double value.
     *
     * @return the parsed double
     * @throws QuestException when there is no part left, or it can't be parsed as double
     */
    default double getDouble() throws QuestException {
        return getDouble(next(), 0);
    }

    /**
     * Parses the string as double.
     *
     * @param string the string to parse as double
     * @param def    the fallback to use when the string is null
     * @return parsed double or fallback
     * @throws QuestException when the string can't be parsed as double
     */
    default double getDouble(@Nullable final String string, final double def) throws QuestException {
        if (string == null) {
            return def;
        }
        return parseNumber(string, Double::parseDouble).doubleValue();
    }
}
