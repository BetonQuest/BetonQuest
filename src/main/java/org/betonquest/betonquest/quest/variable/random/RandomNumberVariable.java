package org.betonquest.betonquest.quest.variable.random;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.exception.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This variable resolves into a random value (inclusive arguments).
 * Note that it will return a different value for each call.
 */
public class RandomNumberVariable implements NullableVariable {

    /**
     * The random instance to use.
     */
    private final ThreadLocalRandom random;

    /**
     * The lower bar of the random amount.
     */
    private final VariableNumber low;

    /**
     * The higher bar of the random amount.
     */
    private final VariableNumber high;

    /**
     * If the value should be returned as {@code double}.
     */
    private final boolean fractional;

    /**
     * The digit amount to round to in fractional mode stored in pattern.
     */
    @Nullable
    private final DecimalFormat format;

    /**
     * Creates a new {@link RandomNumberVariable}.
     *
     * @param random     the random instance to use
     * @param low        the lower bar of the random amount
     * @param high       the higher bar of the random amount
     * @param fractional if the value should be returned as {@code double}
     * @param format     the digit amount to round to in fractional mode stored in pattern
     */
    public RandomNumberVariable(final ThreadLocalRandom random, final VariableNumber low, final VariableNumber high, final boolean fractional, @Nullable final DecimalFormat format) {
        this.random = random;
        this.low = low;
        this.high = high;
        this.fractional = fractional;
        this.format = format;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        try {
            if (fractional) {
                final double lowValue = low.getValue(profile).doubleValue();
                final double highValue = high.getValue(profile).doubleValue();
                final double value = random.nextDouble(Math.min(lowValue, highValue), Math.max(lowValue, highValue));
                if (format != null) {
                    return format.format(value);
                }
                return String.valueOf(value);
            } else {
                return String.valueOf(random.nextInt(
                        low.getValue(profile).intValue(), high.getValue(profile).intValue() + 1));
            }
        } catch (final IllegalArgumentException e) {
            return "";
        }
    }
}
