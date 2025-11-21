package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link ZNPCsPlusIntegrator} instances.
 */
public class ZNPCsPlusIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public ZNPCsPlusIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new ZNPCsPlusIntegrator();
    }
}
