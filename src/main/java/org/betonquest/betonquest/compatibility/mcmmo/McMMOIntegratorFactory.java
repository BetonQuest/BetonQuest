package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link McMMOIntegrator} instances.
 */
public class McMMOIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public McMMOIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new McMMOIntegrator();
    }
}
