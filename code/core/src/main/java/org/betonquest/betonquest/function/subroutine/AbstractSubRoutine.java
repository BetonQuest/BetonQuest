package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionDefinition;
import org.betonquest.betonquest.api.function.FunctionExpression;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.lib.function.assignment.DefaultFallbackAssignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a subroutine in a math function.
 */
public abstract class AbstractSubRoutine implements MathFunction {

    /**
     * The parameters of the subroutine.
     */
    private final List<Parameter> parameters;

    /**
     * Whether the subroutine accepts any number of arguments.
     */
    private final boolean anyNumberOfArguments;

    /**
     * Creates a new instance of the subroutine.
     *
     * @param anyNumberOfArguments whether the subroutine accepts any number of arguments
     * @param parameters           the parameters of the subroutine
     */
    protected AbstractSubRoutine(final boolean anyNumberOfArguments, final Parameter... parameters) {
        this.anyNumberOfArguments = anyNumberOfArguments;
        this.parameters = List.of(parameters);
    }

    /**
     * Helper method to create a parameter with the given name and {@link DefaultFallbackAssignment}.
     *
     * @param name the name of the parameter
     * @return the parameter
     */
    protected static Parameter param(final String name) {
        return param(name, new DefaultFallbackAssignment());
    }

    /**
     * Helper method to create a parameter with the given name and default value.
     *
     * @param name     the name of the parameter
     * @param defValue the default value of the parameter
     * @return the parameter
     */
    protected static Parameter param(final String name, final FunctionAssignment defValue) {
        return new Parameter(name, defValue);
    }

    @Override
    public final FunctionDefinition definition() {
        return arguments -> {
            final long requiredParameters = parameters.stream().filter(param -> param.defValue() instanceof DefaultFallbackAssignment).count();
            if (arguments.size() < requiredParameters) {
                throw new QuestException("Missing '%s' parameter(s) for subroutine definition".formatted(requiredParameters - arguments.size()));
            }
            final Map<String, FunctionAssignment> assignments = new HashMap<>();
            for (int i = 0; i < parameters.size(); i++) {
                assignments.put(parameters.get(i).name(), arguments.size() > i ? arguments.get(i) : parameters.get(i).defValue());
            }
            if (anyNumberOfArguments && arguments.size() > parameters.size()) {
                for (int i = parameters.size(); i < arguments.size(); i++) {
                    assignments.put("n" + i, arguments.get(i));
                }
            }
            return assignments;
        };
    }

    @Override
    public final FunctionExpression expression() {
        return this::evaluateSubroutine;
    }

    @Override
    public final FunctionAssignment evaluate(final FunctionProvider functions, final List<FunctionAssignment> assignments) throws QuestException {
        return evaluateSubroutine(functions, definition().assign(assignments));
    }

    /**
     * Evaluates the subroutine with the given arguments.
     *
     * @param functionProvider the function provider
     * @param arguments        the arguments to evaluate the subroutine with
     * @return the result of the subroutine
     * @throws QuestException if an error occurs during the evaluation
     */
    public abstract FunctionAssignment evaluateSubroutine(FunctionProvider functionProvider, Map<String, FunctionAssignment> arguments) throws QuestException;

    /**
     * Represents a parameter of a subroutine.
     *
     * @param name     the name of the parameter
     * @param defValue the default value of the parameter
     */
    protected record Parameter(String name, FunctionAssignment defValue) {

    }
}
