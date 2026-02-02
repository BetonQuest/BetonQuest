package org.betonquest.betonquest.kernel.registry;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.TypeFactory;

/**
 * Stores the type factories to create Objects from Instructions that can be used in BetonQuest.
 *
 * @param <F> the type to be produced from the stored type factory
 */
public class FactoryTypeRegistry<F> extends FactoryRegistry<TypeFactory<F>> implements FeatureTypeRegistry<F> {

    /**
     * Create a new type registry.
     *
     * @param log      the logger that will be used for logging
     * @param typeName the name of the type to use in the register log message
     */
    public FactoryTypeRegistry(final BetonQuestLogger log, final String typeName) {
        super(log, typeName);
    }
}
