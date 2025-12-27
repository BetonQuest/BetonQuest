package org.betonquest.betonquest.quest.placeholder.math;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholder;
import org.betonquest.betonquest.util.math.tokens.Token;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * This placeholder evaluates the given calculation and returns the result.
 */
public class MathPlaceholder implements NullablePlaceholder {
    /**
     * The full calculation token.
     */
    @SuppressWarnings("deprecation")
    private final Token calculation;

    /**
     * Create a math placeholder from the given calculation.
     *
     * @param calculation calculation to parse
     */
    @SuppressWarnings("deprecation")
    public MathPlaceholder(final Token calculation) {
        this.calculation = calculation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final double value;
        try {
            value = this.calculation.resolve(profile);
        } catch (final QuestException e) {
            throw new QuestException("Error while resolving math placeholder: " + e.getMessage(), e);
        }
        if (value % 1 == 0) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.valueOf(value);
    }
}
