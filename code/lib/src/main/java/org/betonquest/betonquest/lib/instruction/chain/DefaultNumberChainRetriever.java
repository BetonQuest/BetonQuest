package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.instruction.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.NumberArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.NumberChainRetriever;
import org.betonquest.betonquest.lib.instruction.argument.DefaultNumberArgumentParser;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of the {@link NumberChainRetriever} interface
 * using {@link DefaultDecoratableChainRetriever} as a base.
 */
public class DefaultNumberChainRetriever extends DefaultDecoratableChainRetriever<Number> implements NumberChainRetriever {

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction  the instruction used to retrieve the variable
     * @param argument     the argument parser
     * @param defaultValue the nullable default value to use if the instruction fails
     */
    public DefaultNumberChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<Number> argument, @Nullable final Number defaultValue) {
        super(instruction, argument, defaultValue);
    }

    private NumberArgumentParser decoratable() {
        return new DefaultNumberArgumentParser(argument);
    }

    @Override
    public NumberChainRetriever def(final Number defaultValue) {
        return new DefaultNumberChainRetriever(instruction, argument, defaultValue);
    }

    @Override
    public NumberChainRetriever validate(final ValueValidator<Number> validator) {
        return new DefaultNumberChainRetriever(instruction, decoratable().validate(validator), defaultValue);
    }

    @Override
    public NumberChainRetriever validate(final ValueValidator<Number> validator, final String errorMessage) {
        return new DefaultNumberChainRetriever(instruction, decoratable().validate(validator, errorMessage), defaultValue);
    }

    @Override
    public NumberChainRetriever prefilter(final String expected, final Number fixedValue) {
        return new DefaultNumberChainRetriever(instruction, decoratable().prefilter(expected, fixedValue), defaultValue);
    }

    @Override
    public NumberChainRetriever atLeast(final Number inclusiveMin) {
        return new DefaultNumberChainRetriever(instruction, decoratable().atLeast(inclusiveMin), defaultValue);
    }

    @Override
    public NumberChainRetriever atMost(final Number inclusiveMax) {
        return new DefaultNumberChainRetriever(instruction, decoratable().atMost(inclusiveMax), defaultValue);
    }

    @Override
    public NumberChainRetriever inRange(final Number inclusiveMin, final Number exclusiveMax) {
        return new DefaultNumberChainRetriever(instruction, decoratable().inRange(inclusiveMin, exclusiveMax), defaultValue);
    }
}
