package org.betonquest.betonquest.api.kernel;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;

/**
 * Stores the implementation factories for Quest Types.
 *
 * @param <P> the player variant of the type
 * @param <S> the playerless variant of the type
 */
public interface CoreQuestRegistry<P, S> {
    /**
     * Registers a type that does not support playerless execution with its name
     * and a player factory to create new player instances.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the type
     */
    void register(String name, PlayerQuestFactory<P> factory);

    /**
     * Registers a type and a factory to create new playerless instances.
     *
     * @param name              the name of the type
     * @param playerlessFactory the playerless factory to create the type
     */
    void register(String name, PlayerlessQuestFactory<S> playerlessFactory);

    /**
     * Registers a type with its name and a single factory to create both player and playerless instances.
     *
     * @param name    the name of the type
     * @param factory the factory to create the player and playerless variant
     * @param <C>     the type of factory that creates both normal and playerless instances of the type
     */
    <C extends PlayerQuestFactory<P> & PlayerlessQuestFactory<S>> void registerCombined(String name, C factory);

    /**
     * Registers a type with its name and two factories to create player and playerless instances.
     *
     * @param name              the name of the type
     * @param playerFactory     the player factory to create the type
     * @param playerlessFactory the playerless factory to create the type
     */
    void register(String name, PlayerQuestFactory<P> playerFactory, PlayerlessQuestFactory<S> playerlessFactory);
}
