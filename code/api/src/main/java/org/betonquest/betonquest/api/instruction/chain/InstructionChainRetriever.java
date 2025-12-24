package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;

/**
 * In the last step of the instruction chain, the variable is retrieved.
 * In this step the type of the argument is already known.
 *
 * @param <T> the type of the variable
 * @see InstructionChainParser
 */
public interface InstructionChainRetriever<T> {

    /**
     * Retrieves the variable for the given type and the given settings in the chain.
     *
     * @return the variable
     * @throws QuestException if the variable could not be resolved
     */
    @Contract("-> new")
    Variable<T> get() throws QuestException;

    /**
     * Retrieves the variable for a list of the given type and the given settings in the chain.
     *
     * @return the variable
     * @throws QuestException if the list could not be resolved
     */
    @Contract("-> new")
    Variable<List<T>> getList() throws QuestException;

    /**
     * Retrieves the {@link Variable} for the given type and the given settings in the chain.
     * Looking for a key-value argument and returning a {@link Variable} wrapping its value in an {@link Optional}
     *
     * @param argumentKey the argument key
     * @return an optional of the variable
     * @throws QuestException if the variable could not be resolved
     */
    @Contract("!null -> new")
    Optional<Variable<T>> get(String argumentKey) throws QuestException;

    /**
     * Retrieves the {@link Variable} for the given type and the given settings in the chain.
     * Looking for a key-value argument and returning a {@link Variable} wrapping its value in an {@link Optional}
     *
     * @param argumentKey  the argument key
     * @param defaultValue the default value to return if the argument is not present
     * @return the variable
     * @throws QuestException if the variable could not be resolved
     */
    @Contract("!null, !null -> new")
    Variable<T> get(String argumentKey, T defaultValue) throws QuestException;

    /**
     * Retrieves the {@link Variable} for a {@link List} of the given type {@link T} and the given settings in the chain.
     *
     * @param argumentKey the argument key
     * @return an optional of the variable
     * @throws QuestException if the variable could not be resolved
     */
    @Contract("!null -> new")
    Optional<Variable<List<T>>> getList(String argumentKey) throws QuestException;

    /**
     * Retrieves the {@link Variable} for a {@link List} of the given type {@link T} and the given settings in the chain.
     *
     * @param argumentKey  the argument key
     * @param defaultValue the default value to return if the argument is not present
     * @return the variable
     * @throws QuestException if the variable could not be resolved
     */
    @Contract("!null, !null -> new")
    Variable<List<T>> getList(String argumentKey, List<T> defaultValue) throws QuestException;
}
