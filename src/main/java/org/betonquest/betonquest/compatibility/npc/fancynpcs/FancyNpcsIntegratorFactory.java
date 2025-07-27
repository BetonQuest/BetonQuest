package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link FancyNpcsIntegrator} instances.
 */
public class FancyNpcsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public FancyNpcsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new FancyNpcsIntegrator();
    }
}
