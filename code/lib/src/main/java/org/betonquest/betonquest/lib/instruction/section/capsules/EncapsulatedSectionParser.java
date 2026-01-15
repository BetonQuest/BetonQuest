package org.betonquest.betonquest.lib.instruction.section.capsules;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SubSectionArgumentParser;

/**
 * The wrapper for a {@link SubSectionArgumentParser} to be accessed like a {@link SimpleArgumentParser}.
 *
 * @param <T> the type of the resolved value.
 */
public class EncapsulatedSectionParser<T> implements SimpleArgumentParser<T> {

    /**
     * The parent instruction.
     */
    private final SectionInstruction parentInstruction;

    /**
     * The parser to use.
     */
    private final SubSectionArgumentParser<T> parser;

    /**
     * Creates a new EncapsuledSectionParser.
     *
     * @param parentInstruction the parent instruction.
     * @param parser            the parser to use.
     */
    public EncapsulatedSectionParser(final SectionInstruction parentInstruction, final SubSectionArgumentParser<T> parser) {
        this.parentInstruction = parentInstruction;
        this.parser = parser;
    }

    @Override
    public T apply(final String path) throws QuestException {
        final SectionInstruction subSectionInstruction = parentInstruction.subSection(path);
        return parser.parse(subSectionInstruction);
    }
}
