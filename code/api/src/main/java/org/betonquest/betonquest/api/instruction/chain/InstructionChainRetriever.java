package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;

/**
 * In the last step of the instruction chain, the argument is retrieved.
 * In this step the type of the argument is already known.
 *
 * @param <T> the type of the argument
 * @see InstructionChainParser
 */
public interface InstructionChainRetriever<T> {

    /**
     * Retrieves the argument for the given type and the given settings in the chain.
     *
     * @return the argument
     * @throws QuestException if the argument could not be resolved
     */
    @Contract("-> new")
    Argument<T> get() throws QuestException;

    /**
     * Retrieves the argument for a list of the given type and the given settings in the chain.
     *
     * @return the argument
     * @throws QuestException if the list could not be resolved
     */
    @Contract("-> new")
    Argument<List<T>> getList() throws QuestException;

    /**
     * Retrieves the {@link Argument} for the given type and the given settings in the chain.
     * Looking for a key-value argument and returning a {@link Argument} wrapping its value in an {@link Optional}
     *
     * @param argumentKey the argument key
     * @return an optional of the argument
     * @throws QuestException if the argument could not be resolved
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
     */
    @Contract("!null, !null -> new")
    Argument<T> get(String argumentKey, T defaultValue) throws QuestException;

    /**
     * Retrieves the {@link Argument} for a {@link List} of the given type {@link T} and the given settings in the chain.
     *
     * @param argumentKey the argument key
     * @return an optional of the argument
     * @throws QuestException if the argument could not be resolved
     */
    @Contract("!null -> new")
    Optional<Argument<List<T>>> getList(String argumentKey) throws QuestException;

    /**
     * Retrieves the {@link Argument} for a {@link List} of the given type {@link T} and the given settings in the chain.
     *
     * @param argumentKey  the argument key
     * @param defaultValue the default value to return if the argument is not present
     * @return the argument
     * @throws QuestException if the argument could not be resolved
     */
    @Contract("!null, !null -> new")
    Argument<List<T>> getList(String argumentKey, List<T> defaultValue) throws QuestException;

    /**
     * Retrieves the {@link Argument} for the given type and interprets it as a flag.
     *
     * @param argumentKey          the flag's key
     * @param presenceDefaultValue the value for the flags undefined state
     * @return an argument for the optional flag
     * @throws QuestException if the argument could not be resolved
     * @see org.betonquest.betonquest.api.instruction.FlagState
     * @see ChainableInstruction#getFlag(String, InstructionArgumentParser, Object)
     */
    FlagArgument<T> getFlag(String argumentKey, T presenceDefaultValue) throws QuestException;
}
