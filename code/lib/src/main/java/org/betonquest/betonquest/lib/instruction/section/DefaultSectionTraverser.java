package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.ListSectionParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SectionParser;
import org.betonquest.betonquest.api.instruction.section.SectionTraverser;

import java.util.List;

/**
 * Default implementation of {@link SectionTraverser}.
 */
public class DefaultSectionTraverser implements SectionTraverser {

    /**
     * The instruction used to retrieve the section.
     */
    private final SectionInstruction instruction;

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
    public DefaultSectionTraverser(final SectionInstruction instruction, final ArgumentParsers parsers) {
        this.instruction = instruction;
        this.parsers = parsers;
    }

    @Override
    public SectionParser value(final String... sectionPath) {
        return new DefaultSectionParser(instruction, parsers, () -> List.of(sectionPath));
    }

    @Override
    public ListSectionParser list(final String... sectionPath) {
        return new DefaultListSectionParser(instruction, parsers, () -> List.of(sectionPath));
    }
}
