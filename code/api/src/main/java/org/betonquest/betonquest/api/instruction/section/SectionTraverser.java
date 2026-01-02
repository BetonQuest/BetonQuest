package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.instruction.section.path.SectionParser;
import org.betonquest.betonquest.api.instruction.section.subsection.ListSubSectionParser;
import org.betonquest.betonquest.api.instruction.section.subsection.NamedSubSectionParser;
import org.betonquest.betonquest.api.instruction.section.subsection.RawSubSectionParser;

/**
 * Describes a traversable section based on an {@link SectionInstruction}.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface SectionTraverser {

    /**
     * Target a key-value subsection with a path.
     *
     * @param sectionPath the path to the key-value section
     * @return a new {@link SectionParser} to parse the subsection
     */
    SectionParser value(String... sectionPath);

    /**
     * Target an entire configuration subsection with a path.
     *
     * @param sectionPath the path to the configuration section
     * @return a new {@link RawSubSectionParser} to parse the subsection
     */
    RawSubSectionParser section(String... sectionPath);

    /**
     * Target a list subsection with a path.
     *
     * @param sectionPath the path to the list section
     * @return a new {@link ListSubSectionParser} to parse the subsection
     */
    ListSubSectionParser list(String... sectionPath);

    /**
     * Target subsection A containing a number of named subsections B with a path to A.
     *
     * @param sectionPath the path to the parent section
     * @return a new {@link NamedSubSectionParser} to parse the subsections
     */
    NamedSubSectionParser namedSections(String... sectionPath);
}
