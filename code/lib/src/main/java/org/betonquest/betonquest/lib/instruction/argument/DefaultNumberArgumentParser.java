package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.NumberArgumentParser;

/**
 * Decoratable {@link InstructionArgumentParser} for numbers.
 */
public class DefaultNumberArgumentParser extends DecoratableArgumentParser<Number> implements NumberArgumentParser {

    /**
     * Create a new decoratable argument parser for an {@link InstructionArgumentParser}.
     *
     * @param argumentParser the wrapped argument parser
     */
    public DefaultNumberArgumentParser(final InstructionArgumentParser<Number> argumentParser) {
        super(argumentParser);
    }

    @Override
    public NumberArgumentParser atLeast(final Number inclusiveMin) {
        return new DefaultNumberArgumentParser(super.validate(
                value -> value.doubleValue() >= inclusiveMin.doubleValue(),
                "Value must be at least '" + inclusiveMin + "', but was %s"
        ));
    }

    @Override
    public NumberArgumentParser atMost(final Number inclusiveMax) {
        return new DefaultNumberArgumentParser(super.validate(
                value -> value.doubleValue() <= inclusiveMax.doubleValue(),
                "Value must be at most '" + inclusiveMax + "', but was %s"
        ));
    }

    @Override
    public NumberArgumentParser inRange(final Number inclusiveMin, final Number exclusiveMax) {
        return new DefaultNumberArgumentParser(super.validate(
                value -> value.doubleValue() >= inclusiveMin.doubleValue()
                        && value.doubleValue() < exclusiveMax.doubleValue(),
                "Value must be at least '" + inclusiveMin + "' and less than '" + exclusiveMax + "', but was %s"
        ));
    }
}
