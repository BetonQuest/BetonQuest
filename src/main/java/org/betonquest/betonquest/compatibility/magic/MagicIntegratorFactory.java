package org.betonquest.betonquest.compatibility.magic;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link MagicIntegrator} instances.
 */
public class MagicIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public MagicIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new MagicIntegrator();
    }
}
