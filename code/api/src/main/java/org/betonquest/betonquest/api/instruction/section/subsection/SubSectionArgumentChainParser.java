package org.betonquest.betonquest.api.instruction.section.subsection;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;

public interface SubSectionArgumentChainParser<T> {

    /**
     * Parses the given section into a value of type T.
     *
     * @param instruction the section to parse via instruction
     * @return the parsed value
     * @throws QuestException if the parsing fails
     */
    T parse(SectionChainInstruction instruction) throws QuestException;
}
