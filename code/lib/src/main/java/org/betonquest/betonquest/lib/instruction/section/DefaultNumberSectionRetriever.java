package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.NumberArgumentParser;
import org.betonquest.betonquest.api.instruction.section.NumberSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
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
     */
    public DefaultNumberSectionRetriever(final SectionChainInstruction instruction, final List<String> rootPath, final InstructionArgumentParser<Number> parser, final boolean pathMode) {
        super(instruction, rootPath, parser, pathMode);
    }

    private NumberArgumentParser decoratable() {
        return new DefaultNumberArgumentParser(parser);
    }

    @Override
    public NumberSectionRetriever validate(final ValueValidator<Number> validator) {
        return new DefaultNumberSectionRetriever(instruction, rootPath, decoratable().validate(validator), pathMode);
    }

    @Override
    public NumberSectionRetriever validate(final ValueValidator<Number> validator, final String errorMessage) {
        return new DefaultNumberSectionRetriever(instruction, rootPath, decoratable().validate(validator, errorMessage), pathMode);
    }

    @Override
    public NumberSectionRetriever prefilter(final String expected, final Number fixedValue) {
        return new DefaultNumberSectionRetriever(instruction, rootPath, decoratable().prefilter(expected, fixedValue), pathMode);
    }

    @Override
    public NumberSectionRetriever atLeast(final int min) {
        return new DefaultNumberSectionRetriever(instruction, rootPath, decoratable().atLeast(min), pathMode);
    }

    @Override
    public NumberSectionRetriever atMost(final int max) {
        return new DefaultNumberSectionRetriever(instruction, rootPath, decoratable().atMost(max), pathMode);
    }

    @Override
    public NumberSectionRetriever inRange(final int min, final int max) {
        return new DefaultNumberSectionRetriever(instruction, rootPath, decoratable().inRange(min, max), pathMode);
    }
}
