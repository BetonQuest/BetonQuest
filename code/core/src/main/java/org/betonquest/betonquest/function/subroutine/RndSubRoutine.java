package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.Map;

/**
 * Calculates a random number between the given min and max.
 */
public class RndSubRoutine extends AbstractSubRoutine {

    /**
     * Creates a new RndSubRoutine.
     */
    public RndSubRoutine() {
        super(false, param("min"), param("max"));
    }

    @Override
    public FunctionAssignment evaluateSubroutine(final FunctionProvider functionProvider, final Map<String, FunctionAssignment> arguments) throws QuestException {
        final Number min = arguments.get("min").asNumber();
        final Number max = arguments.get("max").asNumber();
        return new NumberSourceAssignment(min.doubleValue() + (max.doubleValue() - min.doubleValue()) * Math.random());
    }
}
