package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.instruction.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Default implementation of {@link DecoratableChainRetriever}
 * that uses {@link DefaultInstructionChainRetriever} as a base.
 *
 * @param <T> the variable type
 */
public class DefaultDecoratableChainRetriever<T> extends DefaultInstructionChainRetriever<T> implements DecoratableChainRetriever<T> {

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the variable
     * @param argument    the argument parser
     */
    public DefaultDecoratableChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<T> argument) {
        super(instruction, argument);
    }

    private DecoratedArgumentParser<T> decoratable() {
        return new DecoratableArgumentParser<T>(argument);
    }

    @Override
    public DecoratableChainRetriever<T> validate(final ValueValidator<T> validator) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().validate(validator));
    }

    @Override
    public DecoratableChainRetriever<T> validate(final ValueValidator<T> validator, final String errorMessage) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().validate(validator, errorMessage));
    }

    @Override
    public DecoratableChainRetriever<T> prefilter(final String expected, final T fixedValue) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().prefilter(expected, fixedValue));
    }

    @Override
    public DecoratableChainRetriever<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().prefilterOptional(expected, fixedValue));
    }
}
