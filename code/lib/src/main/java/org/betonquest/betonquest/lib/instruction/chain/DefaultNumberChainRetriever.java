package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.instruction.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.NumberArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.NumberChainRetriever;
import org.betonquest.betonquest.lib.instruction.argument.DefaultNumberArgumentParser;

/**
 * Default implementation of the {@link NumberChainRetriever} interface
 * using {@link DefaultDecoratableChainRetriever} as a base.
 */
public class DefaultNumberChainRetriever extends DefaultDecoratableChainRetriever<Number> implements NumberChainRetriever {

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the variable
     * @param argument    the argument parser
     */
    public DefaultNumberChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<Number> argument) {
        super(instruction, argument);
    }

    private NumberArgumentParser decoratable() {
        return new DefaultNumberArgumentParser(argument);
    }

    @Override
    public NumberChainRetriever validate(final ValueValidator<Number> validator) {
        return new DefaultNumberChainRetriever(instruction, decoratable().validate(validator));
    }

    @Override
    public NumberChainRetriever validate(final ValueValidator<Number> validator, final String errorMessage) {
        return new DefaultNumberChainRetriever(instruction, decoratable().validate(validator, errorMessage));
    }

    @Override
    public NumberChainRetriever prefilter(final String expected, final Number fixedValue) {
        return new DefaultNumberChainRetriever(instruction, decoratable().prefilter(expected, fixedValue));
    }

    @Override
    public NumberChainRetriever atLeast(final Number inclusiveMin) {
        return new DefaultNumberChainRetriever(instruction, decoratable().atLeast(inclusiveMin));
    }

    @Override
    public NumberChainRetriever atMost(final Number inclusiveMax) {
        return new DefaultNumberChainRetriever(instruction, decoratable().atMost(inclusiveMax));
    }

    @Override
    public NumberChainRetriever inRange(final Number inclusiveMin, final Number exclusiveMax) {
        return new DefaultNumberChainRetriever(instruction, decoratable().inRange(inclusiveMin, exclusiveMax));
    }
}
