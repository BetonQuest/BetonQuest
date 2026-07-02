package org.betonquest.betonquest.api.function;

import org.betonquest.betonquest.api.QuestException;

import java.util.List;

/**
 * Represents a mathematical function separated into definition and expression.
 *
 * @since 3.1.0
 */
public interface MathFunction {

    /**
     * Returns the definition of this function.
     *
     * @return the function definition
     * @since 3.1.0
     */
    FunctionDefinition definition();

    /**
     * Returns the expression of this function.
     *
     * @return the function expression
     * @since 3.1.0
     */
    FunctionExpression expression();

    /**
     * Evaluates the function with the given assignments.
     *
     * @param functions   used for referenced functions and subroutines
     * @param assignments the assignments to use
     * @return the result of the evaluation
     * @throws QuestException if the function cannot be evaluated
     * @since 3.1.0
     */
    FunctionAssignment evaluate(FunctionProvider functions, List<FunctionAssignment> assignments) throws QuestException;
}
