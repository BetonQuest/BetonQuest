package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link NexoIntegrator} instances.
 */
public class NexoIntegratorFactory implements IntegratorFactory {

    /**
     * @return a new {@link NexoIntegrator}
     */
    @Override
    public Integrator getIntegrator() {
        return new NexoIntegrator();
    }
}
