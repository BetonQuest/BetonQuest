package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.jetbrains.annotations.Nullable;

import java.lang.Number;

/**
 * A token that is a Variable.
 *
 * @deprecated This should be replaced with a real expression parsing lib
 */
@Deprecated
public class Variable implements Token {

    /**
     * Underlying variable.
     */
    private final DefaultArgument<Number> number;

    /**
     * Creates a new variable token from a variable number.
     *
     * @param number underlying variable
     */
    public Variable(final DefaultArgument<Number> number) {
        this.number = number;
    }

    @Override
    public double resolve(@Nullable final Profile profile) throws QuestException {
        return number.getValue(profile).doubleValue();
    }

    @Override
    public String toString() {
        return number.toString();
    }
}
