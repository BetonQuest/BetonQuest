package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A pair of Number requirement and its value.
 *
 * @see HandlerUtil#getNumberValue(String, String)
 */
public interface NumberValue {

    /**
     * Parses a {@link HandlerUtil#getNumberValue(String, String)} from a key-value-pair as argument.
     *
     * @param key         the instruction's  argument key
     * @param messagePart to put into exceptions to identify what is parsed
     * @param instruction the instruction to parse the number and value from
     * @return the requirement type and the parsed value, or null if absent
     * @throws QuestException if {@code part} of key can't be parsed or is negative
     */
    @Nullable
    static Argument<NumberValue> create(final String key, final String messagePart, final Instruction instruction) throws QuestException {
        return instruction.parse(resolved -> {
            final Map.Entry<Number, Integer> entry = HandlerUtil.getNumberValue(key, messagePart);
            return (NumberValue) new DefaultNumberValue(entry.getKey(), entry.getValue());
        }).get(key).orElse(null);
    }

    /**
     * The comparing mode.
     *
     * @return the mode used for comparing
     */
    Number mode();

    /**
     * The base value to compare against.
     *
     * @return the parsed value
     */
    int value();

    /**
     * Checks {@link Number#isValid(int, int)} with {@link #value()} as base.
     *
     * @param otherValue the value to check
     * @return if the value matches
     */
    boolean isValid(int otherValue);

    /**
     * Default implementation of {@link NumberValue}.
     *
     * @param mode  the comparing mode
     * @param value the parsed value to compare against
     */
    record DefaultNumberValue(Number mode, int value) implements NumberValue {

        @Override
        public boolean isValid(final int otherValue) {
            return mode.isValid(otherValue, value);
        }
    }
}
