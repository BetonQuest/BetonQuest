package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.instruction.ValueValidator;
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
     */
    public DefaultListSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath, final InstructionArgumentParser<List<T>> parser, final boolean pathMode) {
        super(instruction, rootPath, parser, pathMode);
    }

    private DefaultListArgumentParser<T> decoratable() {
        return new DefaultListArgumentParser<>(parser);
    }

    @Override
    public ListSectionRetriever<T> prefilter(final String expected, final List<T> fixedValue) {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().prefilter(expected, fixedValue), pathMode);
    }

    @Override
    public ListSectionRetriever<T> validate(final ValueValidator<List<T>> validator) {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().validate(validator), pathMode);
    }

    @Override
    public ListSectionRetriever<T> validate(final ValueValidator<List<T>> validator, final String errorMessage) {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().validate(validator, errorMessage), pathMode);
    }

    @Override
    public ListSectionRetriever<T> notEmpty() {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().notEmpty(), pathMode);
    }

    @Override
    public ListSectionRetriever<T> distinct() {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().distinct(), pathMode);
    }

    @Override
    public <U> ListSectionRetriever<T> distinct(final Function<T, U> extractor) {
        return new DefaultListSectionRetriever<>(instruction, rootPath, decoratable().distinct(extractor), pathMode);
    }
}
