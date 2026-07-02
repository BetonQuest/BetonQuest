package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionDefinition;
import org.betonquest.betonquest.api.function.FunctionExpression;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;

import java.util.List;

/**
 * Default implementation of {@link MathFunction}.
 *
 * @param definition the definition of the function
 * @param expression the expression of the function
 * @since 3.1.0
 */
public record DefaultMathFunction(FunctionDefinition definition,
                                  FunctionExpression expression) implements MathFunction {

    @Override
    public FunctionAssignment evaluate(final FunctionProvider functions, final List<FunctionAssignment> assignments) throws QuestException {
        return expression().evaluate(functions, definition().assign(assignments));
    }
}
