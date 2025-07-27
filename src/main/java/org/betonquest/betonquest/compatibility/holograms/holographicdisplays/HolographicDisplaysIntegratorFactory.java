package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link HolographicDisplaysIntegrator} instances.
 */
public class HolographicDisplaysIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public HolographicDisplaysIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new HolographicDisplaysIntegrator();
    }
}
