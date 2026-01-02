package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.bukkit.configuration.ConfigurationSection;

/**
 * The retriever endpoint of the section instruction chain.
 */
public interface SectionChainInstruction {

    /**
     * Get the section contained in this instruction.
     *
     * @return the section
     */
    ConfigurationSection getSection();

    /**
     * Read a value from the section.
     *
     * @param path   the path to the value
     * @param parser the parser to use to parse the value
     * @param <T>    the type of the value
     * @return the parsed value
     * @throws QuestException if the value cannot be parsed
     */
    <T> Argument<T> get(String path, InstructionArgumentParser<T> parser) throws QuestException;

    /**
     * Read an optional value from the section.
     *
     * @param path         the path to the value
     * @param parser       the parser to use to parse the value
     * @param defaultValue the default value to return if the value is not present
     * @param <T>          the type of the value
     * @return the parsed value
     * @throws QuestException if the value cannot be parsed
     */
    <T> Argument<T> getOptional(String path, InstructionArgumentParser<T> parser, T defaultValue) throws QuestException;
}
