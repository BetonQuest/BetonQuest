package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link CraftEngineIntegrator} instances.
 */
public class CraftEngineIntegratorFactory implements IntegratorFactory {

    /**
     * Default constructor for CraftEngineIntegratorFactory.
     */
    public CraftEngineIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new CraftEngineIntegrator();
    }
}
