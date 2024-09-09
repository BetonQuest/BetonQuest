package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores the factories to create Objects that can be used in BetonQuest.
 *
 * @param <F> the factory type to be stored
 */
public class FactoryRegistry<F> {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * Name of the type to display in register log.
     */
    protected final String typeName;

    /**
     * Map of registered factories.
     */
    protected final Map<String, F> types = new HashMap<>();

    /**
     * Create a new type registry.
     *
     * @param log      the logger that will be used for logging
     * @param typeName the name of the type to use in the register log message
     */
    public FactoryRegistry(final BetonQuestLogger log, final String typeName) {
        this.log = log;
        this.typeName = typeName;
    }

    /**
     * Registers a type that does not support playerless execution with its name
     * and a player factory to create new player instances.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the type
     */
    public void register(final String name, final F factory) {
        log.debug("Registering " + name + " " + typeName + " type");
        types.put(name, factory);
    }

    /**
     * Fetches the factory to create the type registered with the given name.
     *
     * @param name the name of the type
     * @return a factory to create the type
     */
    @Nullable
    public F getFactory(final String name) {
        return types.get(name);
    }

    /**
     * Gets the keys of all registered types.
     *
     * @return the actual key set
     */
    public Set<String> keySet() {
        return types.keySet();
    }
}
