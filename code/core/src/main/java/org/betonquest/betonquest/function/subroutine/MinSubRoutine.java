package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.Map;
import java.util.Optional;

/**
 * Represents the min function.
 */
public class MinSubRoutine extends AbstractSubRoutine {

    /**
     * Creates a new MinSubRoutine.
     */
    public MinSubRoutine() {
        super(true, param("a", new NumberSourceAssignment(0)));
    }

    @Override
    public FunctionAssignment evaluateSubroutine(final FunctionProvider functionProvider, final Map<String, FunctionAssignment> arguments) throws QuestException {
        final Optional<Number> minNumber = arguments.values().stream().map(FunctionAssignment::asNumber).reduce((a, b) -> a.doubleValue() <= b.doubleValue() ? a : b);
        return new NumberSourceAssignment(minNumber.orElseThrow(() -> new QuestException("Min: No value found, did you pass any arguments")));
    }
}
