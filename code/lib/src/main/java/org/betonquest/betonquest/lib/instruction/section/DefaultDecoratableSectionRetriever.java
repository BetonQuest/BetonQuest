package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.DecoratableSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.ListSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

/**
 * Default implementation of {@link DecoratableSectionRetriever}.
 *
 * @param <T> the type of the section's value
 */
public class DefaultDecoratableSectionRetriever<T> extends DefaultSectionRetriever<T> implements DecoratableSectionRetriever<T> {

    /**
     * Creates a new decoratable section retriever.
     *
     * @param instruction the instruction used to retrieve the section
     * @param rootPath    the root path to the section
     * @param parser      the argument parser
     * @param pathMode    if the parser is in path mode
     * @deprecated for removal in {@code 4.0.0}, use {@link #DefaultDecoratableSectionRetriever(SectionChainInstruction,
     * ValueSource, InstructionArgumentParser, boolean, boolean)}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    public DefaultDecoratableSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath,
                                              final InstructionArgumentParser<T> parser, final boolean pathMode) {
        this(instruction, rootPath, parser, pathMode, false);
    }

    /**
     * Creates a new decoratable section retriever.
     *
     * @param instruction the instruction used to retrieve the section
     * @param rootPath    the root path to the section
     * @param parser      the argument parser
     * @param pathMode    if the parser is in path mode
     * @param cache       if the argument should be cached if it does not contain placeholders
     */
    public DefaultDecoratableSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath,
                                              final InstructionArgumentParser<T> parser, final boolean pathMode, final boolean cache) {
        super(instruction, rootPath, parser, pathMode, true, cache);
    }

    private DecoratedArgumentParser<T> decoratable() {
        return new DecoratableArgumentParser<>(parser);
    }

    private <A> DecoratableSectionRetriever<A> retriever(final InstructionArgumentParser<A> parser) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parser, pathMode, shouldCache);
    }

    @Override
    public ListSectionRetriever<T> list() {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().list(), pathMode, shouldCache);
    }

    @Override
    public <R> DecoratableSectionRetriever<R> collect(final Collector<T, ?, R> collector) {
        return retriever(decoratable().collect(collector));
    }

    @Override
    public <U> DecoratableSectionRetriever<U> map(final QuestFunction<T, U> mapper) {
        return retriever(decoratable().map(mapper));
    }

    @Override
    public DecoratableSectionRetriever<T> validate(final ValueValidator<T> validator) {
        return retriever(decoratable().validate(validator));
    }

    @Override
    public DecoratableSectionRetriever<T> validate(final ValueValidator<T> validator, final String errorMessage) {
        return retriever(decoratable().validate(validator, errorMessage));
    }

    @Override
    public DecoratableSectionRetriever<T> prefilter(final String expected, final T fixedValue) {
        return retriever(decoratable().prefilter(expected, fixedValue));
    }

    @Override
    public DecoratableSectionRetriever<Optional<T>> prefilterOptional(final String expected, final T fixedValue) {
        return retriever(decoratable().prefilterOptional(expected, fixedValue));
    }
}
