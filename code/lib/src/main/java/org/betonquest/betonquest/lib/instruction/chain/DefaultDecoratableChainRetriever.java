package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.api.instruction.chain.ListChainRetriever;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Collector;

/**
 * Default implementation of {@link DecoratableChainRetriever}
 * that uses {@link DefaultInstructionChainRetriever} as a base.
 *
 * @param <T> the type of the argument
 */
public class DefaultDecoratableChainRetriever<T> extends DefaultInstructionChainRetriever<T> implements DecoratableChainRetriever<T> {

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the argument
     * @param argument    the argument parser
     * @deprecated use {@link #DefaultDecoratableChainRetriever(ChainableInstruction, InstructionArgumentParser, boolean)}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    public DefaultDecoratableChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<T> argument) {
        super(instruction, argument, false);
    }

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the argument
     * @param argument    the argument parser
     * @param cache       if the argument should be cached if it does not contain placeholders
     */
    public DefaultDecoratableChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<T> argument, final boolean cache) {
        super(instruction, argument, cache);
    }

    private DecoratedArgumentParser<T> decoratable() {
        return new DecoratableArgumentParser<>(argument);
    }

    @Override
    public DecoratableChainRetriever<T> cache(final boolean cache) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable(), cache);
    }

    @Override
    public ListChainRetriever<T> list() {
        return new DefaultListChainRetriever<>(instruction, decoratable().list(), cache);
    }

    @Override
    public <R> DecoratableChainRetriever<R> collect(final Collector<T, ?, R> collector) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().collect(collector), cache);
    }

    @Override
    public <U> DecoratableChainRetriever<U> map(final QuestFunction<T, U> mapper) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().map(mapper), cache);
    }

    @Override
    public DecoratableChainRetriever<T> validate(final ValueValidator<T> validator) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().validate(validator), cache);
    }

    @Override
    public DecoratableChainRetriever<T> validate(final ValueValidator<T> validator, final String errorMessage) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().validate(validator, errorMessage), cache);
    }

    @Override
    public DecoratableChainRetriever<T> prefilter(final String expected, final T fixedValue) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().prefilter(expected, fixedValue), cache);
    }

    @Override
    public DecoratableChainRetriever<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return new DefaultDecoratableChainRetriever<>(instruction, decoratable().prefilterOptional(expected, fixedValue), cache);
    }
}
