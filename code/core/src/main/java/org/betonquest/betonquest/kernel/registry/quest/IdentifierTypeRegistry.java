package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.factory.IdentifierRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * A default implementation of the {@link IdentifierRegistry} interface.
 */
public class IdentifierTypeRegistry implements IdentifierRegistry {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Map of registered factories.
     */
    private final Map<Class<?>, IdentifierFactory<?>> types;

    /**
     * Create a new type registry.
     *
     * @param log the logger that will be used for logging
     */
    public IdentifierTypeRegistry(final BetonQuestLogger log) {
        this.log = log;
        this.types = new HashMap<>();
    }

    @Override
    public <I extends Identifier> void register(final Class<I> identifierClazz, final IdentifierFactory<I> factory) {
        log.debug("Registering identifier factory '%s' for '%s' type".formatted(factory.getClass().getSimpleName(), identifierClazz.getSimpleName()));
        types.put(identifierClazz, factory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends Identifier> IdentifierFactory<I> getFactory(final Class<I> clazz) throws QuestException {
        final IdentifierFactory<I> factory = (IdentifierFactory<I>) types.get(clazz);
        if (factory == null) {
            throw new QuestException("No registered identifier factory found for '%s' type".formatted(clazz.getSimpleName()));
        }
        return factory;
    }
}
