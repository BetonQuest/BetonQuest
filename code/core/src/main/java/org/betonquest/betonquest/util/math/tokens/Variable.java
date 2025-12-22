package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.api.profile.Profile;
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
    private final DefaultVariable<Number> variableNumber;

    /**
     * Creates a new variable token from a variable number.
     *
     * @param variableNumber underlying variable
     */
    public Variable(final DefaultVariable<Number> variableNumber) {
        this.variableNumber = variableNumber;
    }

    @Override
    public double resolve(@Nullable final Profile profile) throws QuestException {
        return variableNumber.getValue(profile).doubleValue();
    }

    @Override
    public String toString() {
        return variableNumber.toString();
    }
}
