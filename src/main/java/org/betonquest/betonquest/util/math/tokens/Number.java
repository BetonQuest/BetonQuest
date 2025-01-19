package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Token that is just any number.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * <a href="https://github.com/fasseg/exp4j">fasseg/exp4j</a>
 */
@Deprecated
public class Number implements Token {

    /**
     * The value.
     */
    private final double value;

    /**
     * Creates a new number.
     *
     * @param value value of the number
     */
    public Number(final double value) {
        this.value = value;
    }

    @Override
    public double resolve(@Nullable final Profile profile) {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
