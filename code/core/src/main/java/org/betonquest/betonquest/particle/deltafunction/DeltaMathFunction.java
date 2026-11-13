package org.betonquest.betonquest.particle.deltafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.List;

/**
 * A {@link DeltaFunction} that is based on a {@link MathFunction}.
 */
public class DeltaMathFunction implements DeltaFunction {

    /**
     * The function to use.
     */
    private final MathFunction function;

    /**
     * The function provider.
     */
    private final FunctionProvider functionProvider;

    /**
     * Creates a new delta function.
     *
     * @param functionProvider the function provider
     * @param function         the function to use
     */
    public DeltaMathFunction(final FunctionProvider functionProvider, final MathFunction function) {
        this.functionProvider = functionProvider;
        this.function = function;
    }

    @Override
    public Number evaluate(final Number firstDelta, final Number secondDelta) throws QuestException {
        final List<FunctionAssignment> assignments = List.of(new NumberSourceAssignment(firstDelta), new NumberSourceAssignment(secondDelta));
        return function.evaluate(functionProvider, assignments).asNumber();
    }
}
