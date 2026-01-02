package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This class represents a {@link ConfigurationSection} that can be traversed and parsed.
 */
public interface SectionInstruction extends SectionChainInstruction {

    /**
     * Get {@link ArgumentParsers} with commonly used parsers.
     *
     * @return a provider of commonly used parsers
     */
    ArgumentParsers getParsers();

    /**
     * Get the source QuestPackage.
     *
     * @return the package containing this instruction
     */
    QuestPackage getPackage();

    /**
     * Start traversing the section to parse it.
     *
     * @return a traverser to traverse the section
     */
    SectionTraverser read();
}
