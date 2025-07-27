package org.betonquest.betonquest.compatibility.placeholderapi;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link PlaceholderAPIIntegrator} instances.
 */
public class PlaceholderAPIIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public PlaceholderAPIIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new PlaceholderAPIIntegrator();
    }
}
