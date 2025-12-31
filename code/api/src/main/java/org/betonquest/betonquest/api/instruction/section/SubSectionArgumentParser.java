package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.QuestException;

/**
 * This class represents a parser for a subsection to a value of type T.
 *
 * @param <T> the type of the parsed value
 */
@FunctionalInterface
public interface SubSectionArgumentParser<T> {

    /**
     * Parses the entire given section into a value of type T.
     *
     * @param instruction the section to parse via instruction
     * @return the parsed value
     * @throws QuestException if the parsing fails
     */
    T parse(SectionInstruction instruction) throws QuestException;
}
