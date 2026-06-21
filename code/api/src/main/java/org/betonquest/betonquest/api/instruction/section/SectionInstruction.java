package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This class represents a {@link ConfigurationSection} that can be traversed and parsed.
 *
 * @since 3.0.0
 */
public interface SectionInstruction extends SectionChainInstruction {

    /**
     * Get {@link ArgumentParsers} with commonly used parsers.
     *
     * @return a provider of commonly used parsers
     * @since 3.0.0
     */
    ArgumentParsers getParsers();

    /**
     * Get the source QuestPackage.
     *
     * @return the package containing this instruction
     * @since 3.0.0
     */
    QuestPackage getPackage();

    /**
     * Start traversing the section to parse it.
     *
     * @return a traverser to traverse the section
     * @since 3.0.0
     */
    SectionTraverser read();

    /**
     * Get the logger factory to create loggers.
     *
     * @return the logger factory
     * @since 3.0.0
     */
    BetonQuestLoggerFactory getLoggerFactory();

    /**
     * Start a simple chain for the given argument.
     *
     * @param argument the argument to parse
     * @return a new chain parser
     * @since 3.0.0
     */
    InstructionChainParser chainForArgument(String argument);

    /**
     * Create a new instruction for a subsection of this instruction.
     *
     * @param path the path to the subsection
     * @return a new instruction for the subsection
     * @throws QuestException if the subsection does not exist
     * @since 3.0.0
     */
    SectionInstruction subSection(String... path) throws QuestException;

    /**
     * Create a new instruction for the given section but keeping the related package.
     *
     * @param section the section to create the instruction for
     * @return a new instruction for the given section
     * @since 3.0.0
     */
    SectionInstruction cloneWithSection(ConfigurationSection section);
}
