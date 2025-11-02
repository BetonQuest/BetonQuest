package org.betonquest.betonquest.compatibility.protocollib;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link ProtocolLibIntegrator} instances.
 */
public class ProtocolLibIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public ProtocolLibIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new ProtocolLibIntegrator();
    }
}
