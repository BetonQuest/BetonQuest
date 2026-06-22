package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.jetbrains.annotations.Contract;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * An instruction that can be parsed in parts using chained calls.
 *
 * @since 3.0.0
 */
public interface ChainableInstruction {

    /**
     * Find the next argument in the instruction without a key.
     *
     * @param argumentParser the argument parser to use
     * @param <T>            the type of the argument
     * @return the {@link Argument} wrapping the argument
     * @throws QuestException if an error occurs while parsing the argument
     * @since 3.0.0
     */
    @Contract("!null -> new")
    <T> Argument<T> getNext(InstructionArgumentParser<T> argumentParser) throws QuestException;

    /**
     * Find the optional argument in the instruction by its key.
     *
     * @param argumentKey    the key of the argument
     * @param argumentParser the argument parser to use
     * @param <T>            the type of the argument
     * @return an optional of the {@link Argument} wrapping the argument
     * @throws QuestException if an error occurs while parsing the argument
     * @since 3.0.0
     */
    @Contract("!null, !null -> new")
    <T> Optional<Argument<T>> getOptional(String argumentKey, InstructionArgumentParser<T> argumentParser) throws QuestException;

    /**
     * Find the optional argument in the instruction by its key.
     *
     * @param argumentKey  the key of the argument
     * @param defaultValue the default value to return if the argument is not present
     * @param argument     the argument parser to use
     * @param <T>          the type of the argument
     * @return the {@link Argument} wrapping the argument
     * @throws QuestException if an error occurs while parsing the argument
     * @since 3.0.0
     */
    @Contract("!null, !null, !null -> new")
    <T> Argument<T> getOptional(String argumentKey, InstructionArgumentParser<T> argument, T defaultValue) throws QuestException;

    /**
     * Find the optional flag argument in the instruction by its key.
     * Flags have three states of existence:
     * <ul>
     *     <li>
     *         Absence: The argument is not present in the instruction.
     *         <br>The resulting {@link Argument} will contain an empty optional.
     *         <br>{@link Optional#isEmpty()} will resolve to <code>true</code>.
     *     </li>
     *     <li>
     *         Undefined: The argument is present, but has no value defined.
     *         <br>The resulting {@link Argument} will contain the value defined in {@code presenceDefault}.
     *     </li>
     *     <li>
     *         Defined: The argument is present with a defined value.
     *         <br>The resulting {@link Argument} will contain the value parsed by {@code argumentParser}.
     *     </li>
     * </ul>
     *
     * @param argumentKey     the key of the argument
     * @param argumentParser  the argument parser to use
     * @param presenceDefault the default if the flag is present without value
     * @param <T>             the type of the argument
     * @return an optional of the flag argument
     * @throws QuestException if an error occurs while parsing
     * @since 3.0.0
     */
    @Contract("!null, !null, !null -> new")
    <T> FlagArgument<T> getFlag(String argumentKey, InstructionArgumentParser<T> argumentParser, T presenceDefault) throws QuestException;

    /**
     * Get all named arguments in the instruction filtered by the given {@link Predicate} for their keys.
     *
     * @param argumentParser the argument parser to use
     * @param keyFilter      the filter to apply to the keys
     * @param <T>            the type of the argument
     * @return a map of the named arguments
     * @throws QuestException if an error occurs while parsing
     * @since 3.0.0
     */
    @Contract("!null, !null -> new")
    <T> Map<String, Argument<T>> getNamed(InstructionArgumentParser<T> argumentParser, Predicate<String> keyFilter) throws QuestException;
}
