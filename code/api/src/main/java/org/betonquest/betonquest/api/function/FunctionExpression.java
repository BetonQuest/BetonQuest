package org.betonquest.betonquest.api.function;

import org.betonquest.betonquest.api.QuestException;

import java.util.Map;

/**
 * Represents a function expression that can be evaluated with assignments.
 *
 * @since 3.1.0
 */
@FunctionalInterface
public interface FunctionExpression {

    /**
     * Evaluate the expression with the given assignments.
     *
     * @param functions   used for referenced functions and subroutines
     * @param assignments the assignments to use
     * @return the result of the evaluation as a {@link FunctionAssignment}
     * @throws QuestException if the expression cannot be evaluated
     * @since 3.1.0
     */
    FunctionAssignment evaluate(FunctionProvider functions, Map<String, FunctionAssignment> assignments) throws QuestException;
}
