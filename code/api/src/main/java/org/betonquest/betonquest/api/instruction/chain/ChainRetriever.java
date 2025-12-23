package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.instruction.variable.Variable;

import java.util.List;

/**
 * In the last step of the instruction chain, the variable is retrieved.
 * In this step, the targeted argument or argument key and the type of the argument is already known.
 *
 * @param <T> the type of the variable
 * @see ChainStarter
 * @see PlainChainParser
 */
public interface ChainRetriever<T> {

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
     * Sets a default value for the variable.
     *
     * @param defaultValue the default value
     * @return a new {@link ChainRetriever} with the default value set and carrying all previous settings
     */
    ChainRetriever<T> def(T defaultValue);
}
