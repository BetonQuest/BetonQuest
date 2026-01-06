package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link CraftEngineIntegrator} instances.
 */
public class CraftEngineIntegratorFactory implements IntegratorFactory {

    /**
 * The empty default constructor.
 */
    public CraftEngineIntegratorFactory() { }

    @Override
    public Integrator getIntegrator() {
        return new CraftEngineIntegrator();
    }
}
