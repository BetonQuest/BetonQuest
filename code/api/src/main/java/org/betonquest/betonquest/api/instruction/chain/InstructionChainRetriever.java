package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.instruction.variable.Variable;

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
     */
    Variable<T> get();

    /**
     * Retrieves the variable for a list of the given type and the given settings in the chain.
     *
     * @return the variable
     */
    Variable<List<T>> getList();

    /**
     * Retrieves the {@link Variable} for the given type and the given settings in the chain.
     * Looking for a key-value argument and returning a {@link Variable} wrapping its value in an {@link Optional}
     *
     * @param argumentKey the argument key
     * @return the variable
     */
    Variable<Optional<T>> get(String argumentKey);

    /**
     * Retrieves the {@link Variable} for a {@link List} of the given type {@link T} and the given settings in the chain.
     *
     * @param argumentKey the argument key
     * @return the variable
     */
    Variable<Optional<List<T>>> getList(String argumentKey);

    /**
     * Sets a default value for the variable.
     *
     * @param defaultValue the default value
     * @return a new {@link InstructionChainRetriever} with the default value set and carrying all previous settings
     */
    InstructionChainRetriever<T> def(T defaultValue);
}
