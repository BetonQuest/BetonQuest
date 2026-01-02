package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.instruction.section.path.SectionParser;
import org.betonquest.betonquest.api.instruction.section.subsection.SubSectionRedirector;

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
    SectionParser path(String... sectionPath);

    /**
     * Target a configuration subsection with a direction path.
     *
     * @param sectionPath the path to the configuration section
     * @return a new {@link SubSectionRedirector} to parse the subsection
     */
    SubSectionRedirector section(String... sectionPath);
}
