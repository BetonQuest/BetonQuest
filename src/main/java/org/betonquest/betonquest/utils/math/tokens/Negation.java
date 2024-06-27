package org.betonquest.betonquest.utils.math.tokens;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * Negation of another token.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * https://github.com/fasseg/exp4j
 */
@Deprecated
public class Negation implements Token {

    /**
     * Token that is negated.
     */
    private final Token inside;

    /**
     * Creates negation of a token.
     *
     * @param inside token that is negated
     */
    public Negation(final Token inside) {
        this.inside = inside;
    }

    @Override
    public double resolve(@Nullable final Profile profile) throws QuestRuntimeException {
        return -inside.resolve(profile);
    }

    @Override
    public String toString() {
        return '-' + inside.toString();
    }
}
