package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.betonquest.betonquest.api.instruction.section.SectionTraverser;
import org.betonquest.betonquest.api.instruction.section.path.SectionParser;
import org.betonquest.betonquest.lib.instruction.section.path.DefaultSectionParser;

/**
 * Default implementation of {@link SectionTraverser}.
 */
public class DefaultSectionTraverser implements SectionTraverser {

    /**
     * The instruction used to retrieve the section.
     */
    private final SectionChainInstruction instruction;

    /**
     * The parsers used to parse the section.
     */
    private final ArgumentParsers parsers;

    /**
     * Creates a new DefaultSectionTraverser.
     *
     * @param instruction the instruction used to retrieve the section.
     * @param parsers     the parsers used to parse the section.
     */
    public DefaultSectionTraverser(final SectionChainInstruction instruction, final ArgumentParsers parsers) {
        this.instruction = instruction;
        this.parsers = parsers;
    }

    @Override
    public SectionParser path(final String... sectionPath) {
        return new DefaultSectionParser(instruction, parsers, String.join(".", sectionPath));
    }
}
