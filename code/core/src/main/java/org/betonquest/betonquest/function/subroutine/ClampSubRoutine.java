package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;

import java.util.Map;

/**
 * Represents the clamp function.
 */
public class ClampSubRoutine extends AbstractSubRoutine {

    /**
     * Creates a new ClampSubRoutine.
     */
    public ClampSubRoutine() {
        super(false, param("value"), param("min"), param("max"));
    }

    @Override
    public FunctionAssignment evaluateSubroutine(final FunctionProvider functionProvider, final Map<String, FunctionAssignment> arguments) throws QuestException {
        final Number value = arguments.get("value").asNumber();
        final Number min = arguments.get("min").asNumber();
        final Number max = arguments.get("max").asNumber();
        if (min.doubleValue() > max.doubleValue()) {
            throw new QuestException("Clamp: min value '%s' is greater than max value '%s'".formatted(min, max));
        }
        if (value.doubleValue() < min.doubleValue()) {
            return new NumberSourceAssignment(min);
        }
        if (value.doubleValue() > max.doubleValue()) {
            return new NumberSourceAssignment(max);
        }
        return new NumberSourceAssignment(value);
    }
}
