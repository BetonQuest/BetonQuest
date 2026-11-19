package org.betonquest.betonquest.particle.deltafunction;

import org.betonquest.betonquest.api.QuestException;

/**
 * Represents a constant delta function.
 */
public class ConstantDeltaFunction implements DeltaFunction {

    /**
     * A constant delta function that always returns 0.
     */
    public static final DeltaFunction ZERO = new ConstantDeltaFunction(0);

    /**
     * The value to return.
     */
    private final Number value;

    /**
     * Creates a new constant delta function.
     *
     * @param value the value to return
     */
    public ConstantDeltaFunction(final Number value) {
        this.value = value;
    }

    @Override
    public Number evaluate(final Number firstDelta, final Number secondDelta) throws QuestException {
        return value;
    }
}
