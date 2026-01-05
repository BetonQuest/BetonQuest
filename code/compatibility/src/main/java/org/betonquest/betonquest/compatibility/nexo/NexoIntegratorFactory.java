package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link NexoIntegrator} instances.
 */
public class NexoIntegratorFactory implements IntegratorFactory {

    /** The empty default constructor. */
    public NexoIntegratorFactory() { }

    @Override
    public Integrator getIntegrator() {
        return new NexoIntegrator();
    }
}
