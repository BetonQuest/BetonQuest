package org.betonquest.betonquest.api.instruction.section.subsection;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a parser for an entire {@link ConfigurationSection}.
 *
 * @param <T> the type of the parsed object
 */
@FunctionalInterface
public interface SubSectionArgumentParser<T> extends SubSectionArgumentChainParser<T> {

    /**
     * Parses the given section into a value of type T.
     *
     * @param section the section to parse
     * @return the parsed value
     * @throws QuestException if the parsing fails
     */
    T parse(ConfigurationSection section) throws QuestException;

    @Override
    default T parse(final SectionChainInstruction instruction) throws QuestException {
        return parse(instruction.getSection());
    }
}
