package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

/**
 * A token that is a Variable.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * <a href="https://github.com/fasseg/exp4j">fasseg/exp4j</a>
 */
@Deprecated
public class Variable implements Token {

    /**
     * Underlying variable.
     */
    private final VariableNumber variableNumber;

    /**
     * Creates a new variable token from a variable number.
     *
     * @param variableNumber underlying variable
     */
    public Variable(final VariableNumber variableNumber) {
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
