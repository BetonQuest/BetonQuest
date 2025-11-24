package org.betonquest.betonquest.compatibility.worldedit;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link WorldEditIntegrator} instances.
 */
public class WorldEditIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public WorldEditIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new WorldEditIntegrator();
    }
}
