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
 *
 * @since 3.0.0
 */
public interface SectionChainInstruction {

    /**
     * Get the section contained in this instruction.
     *
     * @return the section
     * @since 3.0.0
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
     * @since 3.0.0
     * @deprecated for removal in {@code 4.0.0},
     * use {@link #get(ValueSource, InstructionArgumentParser, boolean, boolean, boolean)}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    <T> Argument<T> get(ValueSource<List<String>> path, InstructionArgumentParser<T> parser, boolean pathMode, boolean earlyValidation) throws QuestException;

    /**
     * Read a value from the section.
     *
     * @param path            the path to the value
     * @param parser          the parser to use to parse the value
     * @param pathMode        whether the parser is in path mode or not
     * @param earlyValidation if the argument parser should perform early validation
     * @param cache           if the argument should be cached if it does not contain placeholders
     * @param <T>             the type of the value
     * @return the parsed value
     * @throws QuestException if the value cannot be parsed
     * @since 3.3.0
     */
    default <T> Argument<T> get(final ValueSource<List<String>> path, final InstructionArgumentParser<T> parser, final boolean pathMode, final boolean earlyValidation, final boolean cache) throws QuestException {
        return get(path, parser, pathMode, earlyValidation);
    }

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
     * @since 3.0.0
     * @deprecated for removal in {@code 4.0.0},
     * use {@link #getOptional(ValueSource, InstructionArgumentParser, boolean, boolean, boolean)}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    <T> Optional<Argument<T>> getOptional(ValueSource<List<String>> path, InstructionArgumentParser<T> parser, boolean pathMode, boolean earlyValidation) throws QuestException;

    /**
     * Read an optional value from the section.
     *
     * @param path            the path to the value
     * @param parser          the parser to use to parse the value
     * @param pathMode        whether the parser is in path mode or not
     * @param earlyValidation if the argument parser should perform early validation
     * @param cache           if the argument should be cached if it does not contain placeholders
     * @param <T>             the type of the value
     * @return the parsed value wrapped in an optional
     * @throws QuestException if the value cannot be parsed
     * @since 3.3.0
     */
    default <T> Optional<Argument<T>> getOptional(final ValueSource<List<String>> path, final InstructionArgumentParser<T> parser, final boolean pathMode, final boolean earlyValidation, final boolean cache) throws QuestException {
        return getOptional(path, parser, pathMode, earlyValidation);
    }

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
     * @since 3.0.0
     * @deprecated for removal in {@code 4.0.0},
     * use {@link #getOptional(ValueSource, InstructionArgumentParser, boolean, boolean, Object, boolean)}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    <T> Argument<T> getOptional(ValueSource<List<String>> path, InstructionArgumentParser<T> parser, boolean pathMode, boolean earlyValidation, T defaultValue) throws QuestException;

    /**
     * Read an optional value from the section.
     *
     * @param path            the path to the value
     * @param parser          the parser to use to parse the value
     * @param pathMode        whether the parser is in path mode or not
     * @param earlyValidation if the argument parser should perform early validation
     * @param defaultValue    the default value to return if the value is not present
     * @param cache           if the argument should be cached if it does not contain placeholders
     * @param <T>             the type of the value
     * @return the parsed value
     * @throws QuestException if the value cannot be parsed
     * @since 3.3.0
     */
    default <T> Argument<T> getOptional(final ValueSource<List<String>> path, final InstructionArgumentParser<T> parser, final boolean pathMode, final boolean earlyValidation, final T defaultValue, final boolean cache) throws QuestException {
        return getOptional(path, parser, pathMode, earlyValidation, defaultValue);
    }
}
