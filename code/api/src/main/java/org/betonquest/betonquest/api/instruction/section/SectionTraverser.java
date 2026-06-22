package org.betonquest.betonquest.api.instruction.section;

/**
 * Describes a traversable section based on an {@link SectionInstruction}.
 *
 * @since 3.0.0
 */
public interface SectionTraverser {

    /**
     * Target a key-value subsection with a path.
     *
     * @param sectionPath the path to the key-value section
     * @return a new {@link SectionParser} to parse the subsection
     * @since 3.0.0
     */
    SectionParser value(String... sectionPath);

    /**
     * Target a list subsection with a path.
     *
     * @param sectionPath the path to the list section
     * @return a new {@link ListSectionParser} to parse the subsection
     * @since 3.0.0
     */
    ListSectionParser list(String... sectionPath);
}
