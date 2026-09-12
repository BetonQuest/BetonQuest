package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.ListSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.lib.instruction.argument.DefaultListArgumentParser;

import java.util.List;
import java.util.function.Function;

/**
 * Default implementation of {@link ListSectionRetriever}.
 *
 * @param <T> the type of the list's elements
 */
public class DefaultListSectionRetriever<T> extends DefaultDecoratableSectionRetriever<List<T>> implements ListSectionRetriever<T> {

    /**
     * Creates a new list section retriever.
     *
     * @param instruction the instruction used to retrieve the section
     * @param rootPath    the root path to the section
     * @param parser      the argument parser
     * @param pathMode    if the parser is in path mode
     * @deprecated for removal in {@code 4.0.0}, use {@link #DefaultListSectionRetriever(SectionChainInstruction,
     * ValueSource, InstructionArgumentParser, boolean, boolean)}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    public DefaultListSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath, final InstructionArgumentParser<List<T>> parser, final boolean pathMode) {
        this(instruction, rootPath, parser, pathMode, false);
    }

    /**
     * Creates a new list section retriever.
     *
     * @param instruction the instruction used to retrieve the section
     * @param rootPath    the root path to the section
     * @param parser      the argument parser
     * @param pathMode    if the parser is in path mode
     * @param cache       if the argument should be cached if it does not contain placeholders
     */
    public DefaultListSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath,
                                       final InstructionArgumentParser<List<T>> parser, final boolean pathMode, final boolean cache) {
        super(instruction, rootPath, parser, pathMode, cache);
    }

    private DefaultListArgumentParser<T> decoratable() {
        return new DefaultListArgumentParser<>(parser);
    }

    private ListSectionRetriever<T> retriever(final DecoratedArgumentParser<List<T>> decoratable) {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable, pathMode, shouldCache);
    }

    @Override
    public ListSectionRetriever<T> prefilter(final String expected, final List<T> fixedValue) {
        return retriever(decoratable().prefilter(expected, fixedValue));
    }

    @Override
    public ListSectionRetriever<T> validate(final ValueValidator<List<T>> validator) {
        return retriever(decoratable().validate(validator));
    }

    @Override
    public ListSectionRetriever<T> validate(final ValueValidator<List<T>> validator, final String errorMessage) {
        return retriever(decoratable().validate(validator, errorMessage));
    }

    @Override
    public ListSectionRetriever<T> notEmpty() {
        return retriever(decoratable().notEmpty());
    }

    @Override
    public ListSectionRetriever<T> distinct() {
        return retriever(decoratable().distinct());
    }

    @Override
    public <U> ListSectionRetriever<T> distinct(final Function<T, U> extractor) {
        return retriever(decoratable().distinct(extractor));
    }
}
