package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapterFactory;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the variable types that can be used in BetonQuest.
 */
public class VariableTypeRegistry extends FactoryRegistry<TypeFactory<VariableAdapter>> {

    /**
     * Create a new variable type registry.
     *
     * @param log the logger that will be used for logging
     */
    public VariableTypeRegistry(final BetonQuestLogger log) {
        super(log, "variable");
    }

    /**
     * Registers a type that does not support playerless execution with its name
     * and a player factory to create new player instances.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the type
     */
    public void register(final String name, final PlayerQuestFactory<PlayerVariable> factory) {
        registerInternal(name, factory, null);
    }

    /**
     * Registers a type and a factory to create new playerless instances.
     *
     * @param name              the name of the type
     * @param playerlessFactory the playerless factory to create the type
     */
    public void register(final String name, final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory) {
        registerInternal(name, null, playerlessFactory);
    }

    /**
     * Registers a type with its name and a single factory to create both player and playerless instances.
     *
     * @param name    the name of the type
     * @param factory the factory to create the player and playerless variant
     * @param <C>     the type of factory that creates both normal and playerless instances of the type
     */
    public <C extends PlayerQuestFactory<PlayerVariable> & PlayerlessQuestFactory<PlayerlessVariable>>
    void registerCombined(final String name, final C factory) {
        register(name, factory, factory);
    }

    /**
     * Registers a type with its name and two factories to create player and playerless instances.
     *
     * @param name              the name of the type
     * @param playerFactory     the player factory to create the type
     * @param playerlessFactory the playerless factory to create the type
     */
    public void register(final String name, final PlayerQuestFactory<PlayerVariable> playerFactory, final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory) {
        registerInternal(name, playerFactory, playerlessFactory);
    }

    /**
     * Either the player factory or the playerless factory has to be present.
     */
    private void registerInternal(final String name, @Nullable final PlayerQuestFactory<PlayerVariable> playerFactory,
                                  @Nullable final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory) {
        log.debug("Registering " + name + " " + typeName + " type");
        types.put(name, new VariableAdapterFactory(playerFactory, playerlessFactory));
    }
}
