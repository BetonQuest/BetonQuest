package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.ListChainRetriever;
import org.betonquest.betonquest.lib.instruction.argument.DefaultListArgumentParser;

import java.util.List;
import java.util.function.Function;

/**
 * Default implementation of the {@link ListChainRetriever} interface
 * using {@link DefaultDecoratableChainRetriever} as a base.
 *
 * @param <T> the type of the list's elements
 */
public class DefaultListChainRetriever<T> extends DefaultDecoratableChainRetriever<List<T>> implements ListChainRetriever<T> {

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the variable
     * @param argument    the argument parser
     */
    public DefaultListChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<List<T>> argument) {
        super(instruction, argument);
    }

    private DefaultListArgumentParser<T> decoratable() {
        return new DefaultListArgumentParser<>(argument);
    }

    @Override
    public ListChainRetriever<T> prefilter(final String expected, final List<T> fixedValue) {
        return new DefaultListChainRetriever<>(instruction, decoratable().prefilter(expected, fixedValue));
    }

    @Override
    public ListChainRetriever<T> validate(final ValueValidator<List<T>> validator, final String errorMessage) {
        return new DefaultListChainRetriever<>(instruction, decoratable().validate(validator, errorMessage));
    }

    @Override
    public ListChainRetriever<T> validate(final ValueValidator<List<T>> validator) {
        return new DefaultListChainRetriever<>(instruction, decoratable().validate(validator));
    }

    @Override
    public ListChainRetriever<T> notEmpty() {
        return new DefaultListChainRetriever<>(instruction, decoratable().notEmpty());
    }

    @Override
    public ListChainRetriever<T> distinct() {
        return new DefaultListChainRetriever<>(instruction, decoratable().distinct());
    }

    @Override
    public <U> ListChainRetriever<T> distinct(final Function<T, U> extractor) {
        return new DefaultListChainRetriever<>(instruction, decoratable().distinct(extractor));
    }
}
