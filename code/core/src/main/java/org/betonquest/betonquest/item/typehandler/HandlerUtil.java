package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.BooleanParser;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.Number;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
    public static String[] getSplit(@Nullable final String argument, final String message, @Language("RegExp") final String splitSymbol) throws QuestException {
        if (argument == null) {
            throw new QuestException(message);
        }
        final String[] split = argument.split(splitSymbol);
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
     * The value will be one in the {@link org.betonquest.betonquest.item.handler.Number#WHATEVER} case.
     *
     * @param part        to parse into one pair
     * @param messagePart to put into exceptions to identify what is parsed
     * @return the requirement type and the parsed value
     * @throws QuestException if {@code part} can't be parsed or is negative
     */
    public static Map.Entry<org.betonquest.betonquest.item.handler.Number, Integer> getNumberValue(final String part, final String messagePart) throws QuestException {
        final String whatEver = "?";
        if (whatEver.equals(part)) {
            return Map.entry(org.betonquest.betonquest.item.handler.Number.WHATEVER, 1);
        }
        final org.betonquest.betonquest.item.handler.Number number;
        if (part.endsWith("-")) {
            number = org.betonquest.betonquest.item.handler.Number.LESS;
        } else if (part.endsWith("+")) {
            number = org.betonquest.betonquest.item.handler.Number.MORE;
        } else {
            return Map.entry(Number.EQUAL, getNotBelowZero(part, messagePart));
        }
        return Map.entry(number, getNotBelowZero(part.substring(0, part.length() - 1), messagePart));
    }

    /**
     * Serializes a component and quotes it to a key in the Instruction format.
     * <p>
     * It will be serialized into MiniMessage format and prefixed with {@code @[minimessage]}
     * as used in the DecidingMessageParser standard implementation.
     * <p>
     * In addition, all quotes will be escaped as parsed in the standard tokenizer.
     *
     * @param key   the instruction key
     * @param value the component to serialize
     * @return the ready to parse key value pair
     */
    public static String toKeyValue(final String key, final Component value) {
        return "\"" + key + ":@[minimessage]" + MiniMessage.miniMessage().serialize(value).replace("\"", "\\\"") + "\"";
    }

    /**
     * Checks if the value of the key parses to {@code true} or equals the key.
     * <p>
     * Used for keywords which may be denied.
     *
     * @param key         the key to get and check for similarity of the data
     * @param instruction the instruction to get the value from
     * @return if the data is true or the key
     * @throws QuestException when the data is neither the key nor "true" or "false"
     */
    public static Argument<Existence> isKeyOrTrue(final String key, final Instruction instruction) throws QuestException {
        return instruction
                .parse(resolved -> new BooleanParser().apply(resolved) ? Existence.REQUIRED : Existence.FORBIDDEN)
                .prefilter(key, Existence.REQUIRED)
                .get(key, Existence.WHATEVER);
    }

    /**
     * Parses a {@link Color} from a string. The color can be a hex value, a number or a dye color.
     *
     * @param color the string to parse
     * @return the parsed color
     * @throws QuestException when the color could not be parsed
     */
    public static Color getColor(final String color) throws QuestException {
        if (color.isBlank()) {
            throw new QuestException("Color is not specified");
        }
        final Optional<Color> rgbColor = getRgbColor(color);
        if (rgbColor.isPresent()) {
            return rgbColor.get();
        }
        return getDyeColor(color);
    }

    private static Optional<Color> getRgbColor(final String string) throws QuestException {
        try {
            if (string.startsWith("#")) {
                return Optional.of(Color.fromRGB(Integer.parseInt(string.substring(1), 16)));
            }
            if (string.matches("-?\\d+")) {
                return Optional.of(Color.fromRGB(Integer.parseInt(string)));
            }
            return Optional.empty();
        } catch (final NumberFormatException e) {
            throw new QuestException("Color could not be parsed as a number: '%s'".formatted(string), e);
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Color is not valid: '%s'".formatted(string), e);
        }
    }

    private static Color getDyeColor(final String string) throws QuestException {
        try {
            return DyeColor.valueOf(string.toUpperCase(Locale.ROOT)).getColor();
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Dye color does not exist: '%s'".formatted(string), e);
        }
    }
}
