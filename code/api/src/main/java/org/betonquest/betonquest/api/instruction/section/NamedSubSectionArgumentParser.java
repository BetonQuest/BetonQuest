package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.QuestException;

/**
 * This class represents a parser for a named subsection to a value of type T.
 *
 * @param <T> the type of the parsed value
 */
@FunctionalInterface
public interface NamedSubSectionArgumentParser<T> {

    /**
     * Parses the entire given section into a value of type T.
     *
     * @param sectionName the name of the section to parse
     * @param instruction the section to parse via instruction
     * @return the parsed value
     * @throws QuestException if the parsing fails
     */
    T parse(String sectionName, SectionInstruction instruction) throws QuestException;
}
