package org.betonquest.betonquest.quest.variable.math;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.util.math.tokens.Token;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * This variable evaluates the given calculation and returns the result.
 */
public class MathVariable implements NullableVariable {

    /**
     * The full calculation token.
     */
    @SuppressWarnings("deprecation")
    private final Token calculation;

    /**
     * Create a math variable from the given calculation.
     *
     * @param calculation calculation to parse
     */
    @SuppressWarnings("deprecation")
    public MathVariable(final Token calculation) {
        this.calculation = calculation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final double value = this.calculation.resolve(profile);
        if (value % 1 == 0) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.valueOf(value);
    }
}
