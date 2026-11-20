package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.Map;

/**
 * Represents a multiplication with pi.
 */
public class PiSubRoutine extends AbstractSubRoutine {

    /**
     * Creates a new PiSubRoutine.
     */
    public PiSubRoutine() {
        super(false, param("x"));
    }

    @Override
    public FunctionAssignment evaluateSubroutine(final FunctionProvider functionProvider, final Map<String, FunctionAssignment> arguments) throws QuestException {
        final Number number = arguments.get("x").asNumber();
        return new NumberSourceAssignment(Math.PI * number.doubleValue());
    }
}
