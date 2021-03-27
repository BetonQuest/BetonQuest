package pl.betoncraft.betonquest.utils.math.tokens;

import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * A token that is a Variable
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * https://github.com/fasseg/exp4j
 */
@Deprecated
public class Variable implements Token {

    /**
     * Underlying variable
     */
    private final VariableNumber variableNumber;

    /**
     * Creates a new variable token from a variable number
     *
     * @param variableNumber underlying variable
     */
    public Variable(final VariableNumber variableNumber) {
        this.variableNumber = variableNumber;
    }

    @Override
    public double resolve(final String playerID) throws QuestRuntimeException {
        return variableNumber.getDouble(playerID);
    }

    @Override
    public String toString() {
        return variableNumber.toString();
    }
}
