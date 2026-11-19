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
     * @deprecated for removal in {@code 4.0.0}, use {@link #getNext(InstructionArgumentParser, boolean)}
     */
    @Contract("!null -> new")
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
    @Contract("!null, _ -> new")
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
     * @deprecated for removal in {@code 4.0.0}, use {@link #getOptional(String, InstructionArgumentParser, boolean)}
     */
    @Contract("!null, !null -> new")
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
    @Contract("!null, !null, _ -> new")
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
     * @deprecated for removal in {@code 4.0.0}, use {@link #getOptional(String, InstructionArgumentParser, Object, boolean)}
     */
    @Contract("!null, !null, !null -> new")
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
    @Contract("!null, !null, !null, _ -> new")
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
     * @deprecated for removal in {@code 4.0.0}, use {@link #getFlag(String, InstructionArgumentParser, Object, boolean)}
     */
    @Contract("!null, !null, !null -> new")
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
    @Contract("!null, !null, !null, _ -> new")
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
    @Contract("!null, !null -> new")
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
    @Contract("!null, !null, _ -> new")
    <T> Map<String, Argument<T>> getNamed(InstructionArgumentParser<T> argumentParser, Predicate<String> keyFilter, boolean cache) throws QuestException;

    /**
     * Checks if an optional argument with the given key exists in the instruction without creating an {@link Argument}.
     * <p>
     * Optional arguments are key-value pairs in the format {@code key:value}.
     * This method only checks for the existence of the key, not whether the value is valid.
     *
     * @param argumentKey the key of the optional argument to check for
     * @return {@code true} if an optional argument with the given key exists, {@code false} otherwise
     * @throws QuestException if an error occurs while looking for the argument
     * @see #getOptional(String, InstructionArgumentParser, boolean)
     * @since 3.4.0
     */
    boolean hasOptional(String argumentKey) throws QuestException;

    /**
     * Checks if a flag argument with the given key exists in the instruction without creating a {@link FlagArgument}.
     * <p>
     * Flag arguments can appear in three forms:
     * <ul>
     *     <li>{@code key} - flag is present without a value</li>
     *     <li>{@code key:value} - flag is present with a value</li>
     *     <li>Not present at all</li>
     * </ul>
     * This method returns {@code true} for both the first and second case.
     *
     * @param argumentKey the key of the flag argument to check for
     * @return {@code true} if a flag with the given key exists (with or without a value), {@code false} otherwise
     * @throws QuestException if an error occurs while looking for the argument
     * @see #getFlag(String, InstructionArgumentParser, Object, boolean)
     * @since 3.4.0
     */
    boolean hasFlag(String argumentKey) throws QuestException;

    /**
     * Checks if a named argument with the given key exists in the instruction without creating an {@link Argument}.
     * <p>
     * Named arguments are key-value pairs that can be retrieved by their key.
     * This method is typically used in combination with {@link #getNamed(InstructionArgumentParser, Predicate, boolean)}
     * to check for specific named arguments.
     *
     * @param argumentKey the key of the named argument to check for
     * @return {@code true} if a named argument with the given key exists, {@code false} otherwise
     * @throws QuestException if an error occurs while looking for the argument
     * @see #getNamed(InstructionArgumentParser, Predicate, boolean)
     * @since 3.4.0
     */
    boolean hasNamed(String argumentKey) throws QuestException;
}
