package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.Utils;
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
     * @throws InstructionParseException if the argument is null or empty
     */
    public static String[] getNNSplit(@Nullable final String argument, final String message, @Language("RegExp") final String splitSymbol) throws InstructionParseException {
        final String[] split = Utils.getNN(argument, message).split(splitSymbol);
        if (split.length == 0) {
            throw new InstructionParseException("Missing values!");
        }
        return split;
    }

    /**
     * Parses a string into non-negative int, using the given message part inside the exception message.
     *
     * @param number      the string to parse
     * @param messagePart to put into exceptions to identify what is parsed
     * @return zero or a positive number
     * @throws InstructionParseException if {@code number} can't be parsed or is negative
     */
    public static int getNotBelowZero(final String number, final String messagePart) throws InstructionParseException {
        try {
            final int parsed = Integer.parseInt(number);
            if (parsed < 0) {
                throw new InstructionParseException(messagePart + " must be a positive integer");
            }
            return parsed;
        } catch (final NumberFormatException exception) {
            throw new InstructionParseException("Could not parse " + messagePart + ": " + number, exception);
        }
    }

    /**
     * Gets a pair of Number requirement and its non-negative int.
     * <p>
     * The value will be one in the {@link QuestItem.Number#WHATEVER} case.
     *
     * @param part        to parse into one pair
     * @param messagePart to put into exceptions to identify what is parsed
     * @return the requirement type and the parsed value
     * @throws InstructionParseException if {@code part} can't be parsed or is negative
     */
    public static Map.Entry<QuestItem.Number, Integer> getNumberValue(final String part, final String messagePart) throws InstructionParseException {
        final QuestItem.Number number;
        if ("?".equals(part)) {
            return Map.entry(QuestItem.Number.WHATEVER, 1);
        } else if (part.endsWith("-")) {
            number = QuestItem.Number.LESS;
        } else if (part.endsWith("+")) {
            number = QuestItem.Number.MORE;
        } else {
            return Map.entry(QuestItem.Number.EQUAL, getNotBelowZero(part, messagePart));
        }
        return Map.entry(number, getNotBelowZero(part.substring(0, part.length() - 1), messagePart));
    }
}
