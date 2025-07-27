package org.betonquest.betonquest.compatibility.heroes;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link HeroesIntegrator} instances.
 */
public class HeroesIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public HeroesIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new HeroesIntegrator();
    }
}
