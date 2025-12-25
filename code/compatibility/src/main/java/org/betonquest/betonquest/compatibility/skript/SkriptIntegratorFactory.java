package org.betonquest.betonquest.compatibility.skript;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link SkriptIntegrator} instances.
 */
public class SkriptIntegratorFactory implements IntegratorFactory {

    /**
     * Creates a new instance of the factory.
     */
    public SkriptIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new SkriptIntegrator();
    }
}
