package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.NumberArgumentParser;
import org.betonquest.betonquest.api.instruction.section.NumberSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.lib.instruction.argument.DefaultNumberArgumentParser;

import java.util.List;

/**
 * Default implementation of {@link NumberSectionRetriever}.
 */
public class DefaultNumberSectionRetriever extends DefaultDecoratableSectionRetriever<Number> implements NumberSectionRetriever {

    /**
     * Creates a new number section retriever.
     *
     * @param instruction the instruction used to retrieve the section
     * @param rootPath    the root path to the section
     * @param parser      the argument parser
     * @param pathMode    if the parser is in path mode
     * @deprecated for removal in {@code 4.0.0}, use {@link #DefaultNumberSectionRetriever(SectionChainInstruction,
     * ValueSource, InstructionArgumentParser, boolean, boolean)}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    public DefaultNumberSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath, final InstructionArgumentParser<Number> parser, final boolean pathMode) {
        this(instruction, rootPath, parser, pathMode, false);
    }

    /**
     * Creates a new number section retriever.
     *
     * @param instruction the instruction used to retrieve the section
     * @param rootPath    the root path to the section
     * @param parser      the argument parser
     * @param pathMode    if the parser is in path mode
     * @param cache       if the argument should be cached if it does not contain placeholders
     */
    public DefaultNumberSectionRetriever(final SectionChainInstruction instruction, final ValueSource<List<String>> rootPath,
                                         final InstructionArgumentParser<Number> parser, final boolean pathMode, final boolean cache) {
        super(instruction, rootPath, parser, pathMode, cache);
    }

    private NumberArgumentParser decoratable() {
        return new DefaultNumberArgumentParser(parser);
    }

    private NumberSectionRetriever retriever(final DecoratedArgumentParser<Number> decoratable) {
        return new DefaultNumberSectionRetriever(instruction, rootPath, decoratable, pathMode, shouldCache);
    }

    @Override
    public NumberSectionRetriever validate(final ValueValidator<Number> validator) {
        return retriever(decoratable().validate(validator));
    }

    @Override
    public NumberSectionRetriever validate(final ValueValidator<Number> validator, final String errorMessage) {
        return retriever(decoratable().validate(validator, errorMessage));
    }

    @Override
    public NumberSectionRetriever prefilter(final String expected, final Number fixedValue) {
        return retriever(decoratable().prefilter(expected, fixedValue));
    }

    @Override
    public NumberSectionRetriever atLeast(final int min) {
        return retriever(decoratable().atLeast(min));
    }

    @Override
    public NumberSectionRetriever atMost(final int max) {
        return retriever(decoratable().atMost(max));
    }

    @Override
    public NumberSectionRetriever inRange(final int min, final int max) {
        return retriever(decoratable().inRange(min, max));
    }
}
