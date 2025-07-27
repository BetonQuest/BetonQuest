package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link DecentHologramsIntegrator} instances.
 */
public class DecentHologramsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public DecentHologramsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new DecentHologramsIntegrator();
    }
}
