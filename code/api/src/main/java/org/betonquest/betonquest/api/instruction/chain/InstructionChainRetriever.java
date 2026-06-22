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
 * In the last step of the instruction chain, the argument is retrieved.
 * In this step the type of the argument is already known.
 *
 * @param <T> the type of the argument
 * @see InstructionChainParser
 * @since 3.0.0
 */
public interface InstructionChainRetriever<T> {

    /**
     * Retrieves the argument for the given type and the given settings in the chain.
     *
     * @return the argument
     * @throws QuestException if the argument could not be resolved
     * @since 3.0.0
     */
    @Contract("-> new")
    Argument<T> get() throws QuestException;

    /**
     * Retrieves the {@link Argument} for the given type and the given settings in the chain.
     * Looking for a key-value argument and returning a {@link Argument} wrapping its value in an {@link Optional}
     *
     * @param argumentKey the argument key
     * @return an optional of the argument
     * @throws QuestException if the argument could not be resolved
     * @since 3.0.0
     */
    @Contract("!null -> new")
    Optional<Argument<T>> get(String argumentKey) throws QuestException;

    /**
     * Retrieves the {@link Argument} for the given type and the given settings in the chain.
     * Looking for a key-value argument and returning a {@link Argument} wrapping its value in an {@link Optional}
     *
     * @param argumentKey  the argument key
     * @param defaultValue the default value to return if the argument is not present
     * @return the argument
     * @throws QuestException if the argument could not be resolved
     * @since 3.0.0
     */
    @Contract("!null, !null -> new")
    Argument<T> get(String argumentKey, T defaultValue) throws QuestException;

    /**
     * Retrieves the {@link Argument} for the given type and interprets it as a flag.
     *
     * @param argumentKey          the flag's key
     * @param presenceDefaultValue the value for the flags undefined state
     * @return an argument for the optional flag
     * @throws QuestException if the argument could not be resolved
     * @see org.betonquest.betonquest.api.instruction.FlagState
     * @see ChainableInstruction#getFlag(String, InstructionArgumentParser, Object)
     * @since 3.0.0
     */
    @Contract("!null, !null -> new")
    FlagArgument<T> getFlag(String argumentKey, T presenceDefaultValue) throws QuestException;

    /**
     * Retrieves all named arguments of the chain as {@link Argument}s with their key as name in a {@link Map}.
     *
     * @return a map of named arguments
     * @throws QuestException an argument could not be resolved
     * @see ChainableInstruction#getNamed(InstructionArgumentParser, Predicate)
     * @since 3.0.0
     */
    @Contract("-> new")
    Map<String, Argument<T>> getNamed() throws QuestException;

    /**
     * Retrieves all targeted named arguments of the chain as {@link Argument}s with their key as name in a {@link Map}.
     * The arguments are filtered by the given {@link Predicate}
     * and only those argument keys that match the filter are returned.
     *
     * @param keyFilter a filter for the keys of the arguments
     * @return a map of named arguments
     * @throws QuestException an argument could not be resolved
     * @see ChainableInstruction#getNamed(InstructionArgumentParser, Predicate)
     * @since 3.0.0
     */
    @Contract("!null -> new")
    Map<String, Argument<T>> getNamed(Predicate<String> keyFilter) throws QuestException;
}
