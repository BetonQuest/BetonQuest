package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.QuestException;
import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Stores implementation factories for features.
 *
 * @param <F> the factory type to be stored
 * @since 3.0.0
 */
public interface FeatureRegistry<F> {

    /**
     * Registers a type factory with its name.
     *
     * @param name    the name of the type
     * @param factory the factory to create the type
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    void register(String name, F factory);

    /**
     * Fetches the first registered factory from the given names.
     *
     * @param names the names of the type
     * @return factory to create the first found type
     * @throws QuestException when there is none factory registered
     * @since 3.0.0
     */
    @Contract(pure = true)
    F getFactory(List<String> names) throws QuestException;

    /**
     * Fetches the stored {@link F} with the given name.
     *
     * @param name the name of the type
     * @return a factory to create the type
     * @throws QuestException when there is no stored type
     * @since 3.0.0
     */
    @Contract(pure = true)
    F getFactory(String name) throws QuestException;
}
