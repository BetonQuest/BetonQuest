package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Optional;

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
     * @param path            the path to the value
     * @param parser          the parser to use to parse the value
     * @param pathMode        whether the parser is in path mode or not
     * @param earlyValidation if the argument parser should perform early validation
     * @param <T>             the type of the value
     * @return the parsed value
     * @throws QuestException if the value cannot be parsed
     */
    <T> Argument<T> get(ValueSource<List<String>> path, InstructionArgumentParser<T> parser, boolean pathMode, boolean earlyValidation) throws QuestException;

    /**
     * Read an optional value from the section.
     *
     * @param path            the path to the value
     * @param parser          the parser to use to parse the value
     * @param pathMode        whether the parser is in path mode or not
     * @param earlyValidation if the argument parser should perform early validation
     * @param <T>             the type of the value
     * @return the parsed value wrapped in an optional
     * @throws QuestException if the value cannot be parsed
     */
    <T> Optional<Argument<T>> getOptional(ValueSource<List<String>> path, InstructionArgumentParser<T> parser, boolean pathMode, boolean earlyValidation) throws QuestException;

    /**
     * Read an optional value from the section.
     *
     * @param path            the path to the value
     * @param parser          the parser to use to parse the value
     * @param pathMode        whether the parser is in path mode or not
     * @param earlyValidation if the argument parser should perform early validation
     * @param defaultValue    the default value to return if the value is not present
     * @param <T>             the type of the value
     * @return the parsed value
     * @throws QuestException if the value cannot be parsed
     */
    <T> Argument<T> getOptional(ValueSource<List<String>> path, InstructionArgumentParser<T> parser, boolean pathMode, boolean earlyValidation, T defaultValue) throws QuestException;
}
