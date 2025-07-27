package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link BreweryIntegrator} instances.
 */
public class BreweryIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public BreweryIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new BreweryIntegrator();
    }
}
