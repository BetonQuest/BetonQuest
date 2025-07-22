package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.Utils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Utils for handler "instruction" parsing.
 */
public final class HandlerUtil {

    private HandlerUtil() {
    }

    /**
     * Splits the argument by the given regex.
     * <p>
     * Throws if the argument is null or the created array empty.
     *
     * @param argument    to check for null and split
     * @param message     of the exception when the argument is null
     * @param splitSymbol regex to split
     * @return non empty string array
     * @throws QuestException if the argument is null or empty
     */
    public static String[] getNNSplit(@Nullable final String argument, final String message, @Language("RegExp") final String splitSymbol) throws QuestException {
        final String[] split = Utils.getNN(argument, message).split(splitSymbol);
        if (split.length == 0) {
            throw new QuestException("Missing values!");
        }
        return split;
    }

    /**
     * Parses a string into non-negative int, using the given message part inside the exception message.
     *
     * @param number      the string to parse
     * @param messagePart to put into exceptions to identify what is parsed
     * @return zero or a positive number
     * @throws QuestException if {@code number} can't be parsed or is negative
     */
    public static int getNotBelowZero(final String number, final String messagePart) throws QuestException {
        try {
            final int parsed = Integer.parseInt(number);
            if (parsed < 0) {
                throw new QuestException(messagePart + " must be a positive integer");
            }
            return parsed;
        } catch (final NumberFormatException exception) {
            throw new QuestException("Could not parse " + messagePart + ": " + number, exception);
        }
    }

    /**
     * Gets a pair of Number requirement and its non-negative int.
     * <p>
     * The value will be one in the {@link Number#WHATEVER} case.
     *
     * @param part        to parse into one pair
     * @param messagePart to put into exceptions to identify what is parsed
     * @return the requirement type and the parsed value
     * @throws QuestException if {@code part} can't be parsed or is negative
     */
    public static Map.Entry<Number, Integer> getNumberValue(final String part, final String messagePart) throws QuestException {
        final Number number;
        final String whatEver = "?";
        if (whatEver.equals(part)) {
            return Map.entry(Number.WHATEVER, 1);
        } else if (part.endsWith("-")) {
            number = Number.LESS;
        } else if (part.endsWith("+")) {
            number = Number.MORE;
        } else {
            return Map.entry(Number.EQUAL, getNotBelowZero(part, messagePart));
        }
        return Map.entry(number, getNotBelowZero(part.substring(0, part.length() - 1), messagePart));
    }
}
