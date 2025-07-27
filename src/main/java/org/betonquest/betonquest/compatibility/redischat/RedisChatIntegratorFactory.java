package org.betonquest.betonquest.compatibility.redischat;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link RedisChatIntegrator} instances.
 */
public class RedisChatIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public RedisChatIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new RedisChatIntegrator();
    }
}
