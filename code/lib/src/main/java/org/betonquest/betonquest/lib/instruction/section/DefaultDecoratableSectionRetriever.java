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
     */
    public DefaultDecoratableSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath,
                                              final InstructionArgumentParser<T> parser, final boolean pathMode) {
        super(instruction, rootPath, parser, pathMode);
    }

    private DecoratedArgumentParser<T> decoratable() {
        return new DecoratableArgumentParser<>(parser);
    }

    @Override
    public ListSectionRetriever<T> list() {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().list(), pathMode);
    }

    @Override
    public <R> DecoratableSectionRetriever<R> collect(final Collector<T, ?, R> collector) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, decoratable().collect(collector), pathMode);
    }

    @Override
    public <U> DecoratableSectionRetriever<U> map(final QuestFunction<T, U> mapper) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, decoratable().map(mapper), pathMode);
    }

    @Override
    public DecoratableSectionRetriever<T> validate(final ValueValidator<T> validator) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, decoratable().validate(validator), pathMode);
    }

    @Override
    public DecoratableSectionRetriever<T> validate(final ValueValidator<T> validator, final String errorMessage) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, decoratable().validate(validator, errorMessage), pathMode);
    }

    @Override
    public DecoratableSectionRetriever<T> prefilter(final String expected, final T fixedValue) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, decoratable().prefilter(expected, fixedValue), pathMode);
    }

    @Override
    public DecoratableSectionRetriever<Optional<T>> prefilterOptional(final String expected, final T fixedValue) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, decoratable().prefilterOptional(expected, fixedValue), pathMode);
    }
}
