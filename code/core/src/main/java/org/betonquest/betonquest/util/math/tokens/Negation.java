package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Negation of another token.
 *
 * @deprecated This should be replaced with a real expression parsing lib
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
    public double resolve(@Nullable final Profile profile) throws QuestException {
        return -inside.resolve(profile);
    }

    @Override
    public String toString() {
        return '-' + inside.toString();
    }
}
