package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Returns the absolute value (see {@link Math#abs(double)}) of a token.
 *
 * @deprecated This should be replaced with a real expression parsing lib
 */
@Deprecated
public class AbsoluteValue implements Token {

    /**
     * Token that is inside.
     */
    private final Token inside;

    /**
     * Constructs a new absolute value.
     *
     * @param inside token that is inside
     */
    public AbsoluteValue(final Token inside) {
        this.inside = inside;
    }

    @Override
    public double resolve(@Nullable final Profile profile) throws QuestException {
        return Math.abs(inside.resolve(profile));
    }

    @Override
    public String toString() {
        return '|' + inside.toString() + '|';
    }
}
