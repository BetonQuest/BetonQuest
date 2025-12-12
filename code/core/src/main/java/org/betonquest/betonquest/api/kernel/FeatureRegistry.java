package org.betonquest.betonquest.api.kernel;

import org.betonquest.betonquest.api.QuestException;

import java.util.List;

/**
 * Stores implementation factories for features.
 *
 * @param <F> the factory type to be stored
 */
public interface FeatureRegistry<F> {

    /**
     * Registers a type that does not support playerless execution with its name
     * and a player factory to create new player instances.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the type
     */
    void register(String name, F factory);

    /**
     * Fetches the first registered factory from the given names.
     *
     * @param names the names of the type
     * @return factory to create the first found type
     * @throws QuestException when there is none factory registered
     */
    F getFactory(List<String> names) throws QuestException;

    /**
     * Fetches the stored {@link F} with the given name.
     *
     * @param name the name of the type
     * @return a factory to create the type
     * @throws QuestException when there is no stored type
     */
    F getFactory(String name) throws QuestException;
}
