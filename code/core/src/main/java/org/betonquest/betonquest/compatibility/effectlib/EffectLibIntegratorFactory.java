package org.betonquest.betonquest.compatibility.effectlib;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link EffectLibIntegrator} instances.
 */
public class EffectLibIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public EffectLibIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new EffectLibIntegrator();
    }
}
