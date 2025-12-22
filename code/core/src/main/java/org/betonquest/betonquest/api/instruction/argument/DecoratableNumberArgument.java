package org.betonquest.betonquest.api.instruction.argument;

/**
 * Decoratable {@link Argument} for numbers.
 */
public class DecoratableNumberArgument extends DecoratableArgument<Number> implements DecoratedNumberArgument {

    /**
     * Create a new {@link DecoratedNumberArgument}.
     *
     * @param argument the {@link Argument} to wrap
     */
    public DecoratableNumberArgument(final Argument<Number> argument) {
        super(argument);
    }

    @Override
    public DecoratedNumberArgument atLeast(final Number inclusiveMin) {
        return new DecoratableNumberArgument(super.validate(
                value -> value.doubleValue() >= inclusiveMin.doubleValue(),
                "Value must be at least '" + inclusiveMin + "', but was %s"));
    }

    @Override
    public DecoratedNumberArgument atMost(final Number inclusiveMax) {
        return new DecoratableNumberArgument(super.validate(
                value -> value.doubleValue() <= inclusiveMax.doubleValue(),
                "Value must be at most '" + inclusiveMax + "', but was %s"));
    }

    @Override
    public DecoratedNumberArgument bounds(final Number inclusiveMin, final Number exclusiveMax) {
        return new DecoratableNumberArgument(super.validate(
                value -> value.doubleValue() >= inclusiveMin.doubleValue()
                        && value.doubleValue() < exclusiveMax.doubleValue(),
                "Value must be at least '" + inclusiveMin + "' and less than '" + exclusiveMax + "', but was %s"
        ));
    }
}
