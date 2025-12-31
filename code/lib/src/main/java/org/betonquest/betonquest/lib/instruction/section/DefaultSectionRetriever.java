package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.betonquest.betonquest.api.instruction.section.SectionRetriever;

import java.util.List;

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
    protected final List<String> rootPath;

    /**
     * The parser used to parse the section.
     */
    protected final InstructionArgumentParser<T> parser;

    /**
     * If the parser is in path mode.
     */
    protected final boolean pathMode;

    /**
     * Creates a new DefaultSectionRetriever.
     *
     * @param instruction the instruction used to retrieve the section.
     * @param rootPath    the root path to the section.
     * @param parser      the parser used to parse the section.
     * @param pathMode    if the parser is in path mode.
     */
    public DefaultSectionRetriever(final SectionChainInstruction instruction, final List<String> rootPath,
                                   final InstructionArgumentParser<T> parser, final boolean pathMode) {
        this.instruction = instruction;
        this.rootPath = rootPath;
        this.pathMode = pathMode;
        this.parser = parser;
    }

    @Override
    public Argument<T> get() throws QuestException {
        return instruction.get(rootPath, parser, pathMode);
    }

    @Override
    public Argument<T> getOptional(final T defaultValue) throws QuestException {
        return instruction.getOptional(rootPath, parser, pathMode, defaultValue);
    }
}
