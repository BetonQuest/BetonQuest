package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.Map;
import java.util.Optional;

/**
 * Represents the max function.
 */
public class MaxSubRoutine extends AbstractSubRoutine {

    /**
     * Creates a new MaxSubRoutine.
     */
    public MaxSubRoutine() {
        super(true, param("a", new NumberSourceAssignment(0)));
    }

    @Override
    public FunctionAssignment evaluateSubroutine(final FunctionProvider functionProvider, final Map<String, FunctionAssignment> arguments) throws QuestException {
        final Optional<Number> maxNumber = arguments.values().stream().map(FunctionAssignment::asNumber).reduce((a, b) -> a.doubleValue() >= b.doubleValue() ? a : b);
        return new NumberSourceAssignment(maxNumber.orElseThrow(() -> new QuestException("Max: No value found, did you pass any arguments")));
    }
}
