package org.betonquest.betonquest.lib.instruction.section.path;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.betonquest.betonquest.api.instruction.section.path.SectionRetriever;

/**
 * Default implementation of {@link SectionRetriever}.
 *
 * @param <T> the type of the section's value
 */
public class DefaultSectionRetriever<T> implements SectionRetriever<T> {

    /**
     * The instruction used to retrieve the section.
     */
    protected final SectionChainInstruction instruction;

    /**
     * The root path to the section.
     */
    protected final String rootPath;

    /**
     * The parser used to parse the section.
     */
    protected final InstructionArgumentParser<T> parser;

    /**
     * Creates a new DefaultSectionRetriever.
     *
     * @param instruction the instruction used to retrieve the section.
     * @param rootPath    the root path to the section.
     * @param parser      the parser used to parse the section.
     */
    public DefaultSectionRetriever(final SectionChainInstruction instruction, final String rootPath, final InstructionArgumentParser<T> parser) {
        this.instruction = instruction;
        this.rootPath = rootPath;
        this.parser = parser;
    }

    @Override
    public Argument<T> get() throws QuestException {
        return instruction.get(rootPath, parser);
    }

    @Override
    public Argument<T> getOptional(final T defaultValue) throws QuestException {
        return instruction.getOptional(rootPath, parser, defaultValue);
    }
}
