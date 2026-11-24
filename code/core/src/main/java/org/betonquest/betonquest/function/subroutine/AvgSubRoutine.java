package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.Map;
import java.util.OptionalDouble;

/**
 * Represents the avg function.
 */
public class AvgSubRoutine extends AbstractSubRoutine {

    /**
     * Creates a new AvgSubRoutine.
     */
    public AvgSubRoutine() {
        super(true, param("a", new NumberSourceAssignment(0)));
    }

    @Override
    public FunctionAssignment evaluateSubroutine(final FunctionProvider functionProvider, final Map<String, FunctionAssignment> arguments) throws QuestException {
        final OptionalDouble average = arguments.values().stream().map(FunctionAssignment::asNumber).mapToDouble(Number::doubleValue).average();
        return new NumberSourceAssignment(average.orElseThrow(() -> new QuestException("Avg: No value found, did you pass any arguments")));
    }
}
