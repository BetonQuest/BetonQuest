package org.betonquest.betonquest.kernel.registry;

import org.betonquest.betonquest.api.kernel.FeatureRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Stores the factories to create Objects that can be used in BetonQuest.
 *
 * @param <F> the factory type to be stored
 */
public class FactoryRegistry<F> implements FeatureRegistry<F> {
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
    protected final Map<String, F> types = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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

    @Override
    public void register(final String name, final F factory) {
        log.debug("Registering " + name + " " + typeName + " type");
        types.put(name, factory);
    }

    @Override
    public F getFactory(final List<String> names) throws QuestException {
        for (final String name : names) {
            final F factory = types.get(name);
            if (factory != null) {
                return factory;
            }
            log.debug(typeName + " '" + name + "' not found. Trying next one...");
        }
        throw new QuestException("No registered " + typeName + " found for: " + names);
    }

    @Override
    public F getFactory(final String name) throws QuestException {
        final F type = types.get(name);
        if (type == null) {
            throw new QuestException("'" + name + "' is not loaded for type '" + typeName
                    + "'! Check if it is spelled correctly!");
        }
        return type;
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
