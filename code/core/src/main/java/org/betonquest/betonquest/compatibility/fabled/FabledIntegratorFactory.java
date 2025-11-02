package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link FabledIntegrator} instances.
 */
public class FabledIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public FabledIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new FabledIntegrator();
    }
}
