package org.betonquest.betonquest.api.instruction.section.subsection;

import org.betonquest.betonquest.api.instruction.section.path.DecoratableSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.path.ListSectionRetriever;

public interface NamedSubSectionParser {

    /**
     * Parses the section into a value of type T using the given parser.
     *
     * @param parser the parser to use
     * @param <T>    the type of the parsed value
     * @return a new {@link DecoratableSectionRetriever} for the parsed value
     */
    <T> ListSectionRetriever<T> parse(SubSectionArgumentChainParser<T> parser);
}
