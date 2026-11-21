package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.Map;

/**
 * Represents the round function.
 */
public class RoundSubRoutine extends AbstractSubRoutine {

    /**
     * Creates a new RoundSubRoutine.
     */
    public RoundSubRoutine() {
        super(false, param("x"), param("precision", new NumberSourceAssignment(0)));
    }

    @Override
    public FunctionAssignment evaluateSubroutine(final FunctionProvider functionProvider, final Map<String, FunctionAssignment> arguments) {
        final Number number = arguments.get("x").asNumber();
        final Number precision = arguments.get("precision").asNumber();
        final double value = number.doubleValue();
        final int precisionValue = precision.intValue();
        final double exp = Math.pow(10, precisionValue);
        final double rounded = Math.round(value * exp) / exp;
        if (precisionValue <= 0) {
            return new NumberSourceAssignment(Math.round(rounded));
        }
        return new NumberSourceAssignment(rounded);
    }
}
