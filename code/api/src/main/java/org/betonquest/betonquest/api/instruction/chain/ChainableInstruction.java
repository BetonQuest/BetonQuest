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
     * @deprecated use {@link #getNext(InstructionArgumentParser, boolean)}
     */
    @Contract("_ -> new")
    @Deprecated(forRemoval = true, since = "3.3.0")
    default <T> Argument<T> getNext(final InstructionArgumentParser<T> argumentParser) throws QuestException {
        return getNext(argumentParser, false);
    }

    /**
     * Find the next argument in the instruction without a key.
     *
     * @param argumentParser the argument parser to use
     * @param cache          if the argument should be cached if it does not contain placeholders
     * @param <T>            the type of the argument
     * @return the {@link Argument} wrapping the argument
     * @throws QuestException if an error occurs while parsing the argument
     * @since 3.3.0
     */
    @Contract("_, _ -> new")
    <T> Argument<T> getNext(InstructionArgumentParser<T> argumentParser, boolean cache) throws QuestException;

    /**
     * Find the optional argument in the instruction by its key.
     *
     * @param argumentKey    the key of the argument
     * @param argumentParser the argument parser to use
     * @param <T>            the type of the argument
     * @return an optional of the {@link Argument} wrapping the argument
     * @throws QuestException if an error occurs while parsing the argument
     * @since 3.0.0
     * @deprecated use {@link #getOptional(String, InstructionArgumentParser, boolean)}
     */
    @Contract("_, _ -> new")
    @Deprecated(forRemoval = true, since = "3.3.0")
    default <T> Optional<Argument<T>> getOptional(final String argumentKey, final InstructionArgumentParser<T> argumentParser) throws QuestException {
        return getOptional(argumentKey, argumentParser, false);
    }

    /**
     * Find the optional argument in the instruction by its key.
     *
     * @param argumentKey    the key of the argument
     * @param argumentParser the argument parser to use
     * @param cache          if the argument should be cached if it does not contain placeholders
     * @param <T>            the type of the argument
     * @return an optional of the {@link Argument} wrapping the argument
     * @throws QuestException if an error occurs while parsing the argument
     * @since 3.3.0
     */
    @Contract("_, _, _ -> new")
    <T> Optional<Argument<T>> getOptional(String argumentKey, InstructionArgumentParser<T> argumentParser, boolean cache) throws QuestException;

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
     * @deprecated use {@link #getOptional(String, InstructionArgumentParser, Object, boolean)}
     */
    @Contract("_, _, _ -> new")
    @Deprecated(forRemoval = true, since = "3.3.0")
    default <T> Argument<T> getOptional(final String argumentKey, final InstructionArgumentParser<T> argument, final T defaultValue) throws QuestException {
        return getOptional(argumentKey, argument, defaultValue, false);
    }

    /**
     * Find the optional argument in the instruction by its key.
     *
     * @param argumentKey  the key of the argument
     * @param defaultValue the default value to return if the argument is not present
     * @param argument     the argument parser to use
     * @param cache        if the argument should be cached if it does not contain placeholders
     * @param <T>          the type of the argument
     * @return the {@link Argument} wrapping the argument
     * @throws QuestException if an error occurs while parsing the argument
     * @since 3.0.0
     */
    @Contract("_, _, _, _ -> new")
    <T> Argument<T> getOptional(String argumentKey, InstructionArgumentParser<T> argument, T defaultValue, boolean cache) throws QuestException;

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
     * @deprecated use {@link #getFlag(String, InstructionArgumentParser, Object, boolean)}
     */
    @Contract("_, _, _ -> new")
    @Deprecated(forRemoval = true, since = "3.3.0")
    default <T> FlagArgument<T> getFlag(final String argumentKey, final InstructionArgumentParser<T> argumentParser, final T presenceDefault) throws QuestException {
        return getFlag(argumentKey, argumentParser, presenceDefault, false);
    }

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
     * @param cache           if the argument should be cached if it does not contain placeholders
     * @param <T>             the type of the argument
     * @return an optional of the flag argument
     * @throws QuestException if an error occurs while parsing
     * @since 3.0.0
     */
    @Contract("_, _, _, _ -> new")
    <T> FlagArgument<T> getFlag(String argumentKey, InstructionArgumentParser<T> argumentParser, T presenceDefault, boolean cache) throws QuestException;

    /**
     * Get all named arguments in the instruction filtered by the given {@link Predicate} for their keys.
     *
     * @param argumentParser the argument parser to use
     * @param keyFilter      the filter to apply to the keys
     * @param <T>            the type of the argument
     * @return a map of the named arguments
     * @throws QuestException if an error occurs while parsing
     * @since 3.0.0
     * @deprecated use {@link #getNamed(InstructionArgumentParser, Predicate, boolean)}
     */
    @Deprecated
    @Contract("_, _ -> new")
    default <T> Map<String, Argument<T>> getNamed(final InstructionArgumentParser<T> argumentParser, final Predicate<String> keyFilter) throws QuestException {
        return getNamed(argumentParser, keyFilter, false);
    }

    /**
     * Get all named arguments in the instruction filtered by the given {@link Predicate} for their keys.
     *
     * @param argumentParser the argument parser to use
     * @param keyFilter      the filter to apply to the keys
     * @param cache          if the argument should be cached if it does not contain placeholders
     * @param <T>            the type of the argument
     * @return a map of the named arguments
     * @throws QuestException if an error occurs while parsing
     * @since 3.0.0
     */
    @Contract("_, _, _ -> new")
    <T> Map<String, Argument<T>> getNamed(InstructionArgumentParser<T> argumentParser, Predicate<String> keyFilter, boolean cache) throws QuestException;
}
