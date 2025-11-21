package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;

/**
 * Stores the Interceptors that can be used in BetonQuest.
 */
public class InterceptorRegistry extends FactoryRegistry<InterceptorFactory> {

    /**
     * Create a new Interceptor registry.
     *
     * @param log the logger that will be used for logging
     */
    public InterceptorRegistry(final BetonQuestLogger log) {
        super(log, "Interceptor");
    }
}
