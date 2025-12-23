package org.betonquest.betonquest.compatibility.mythicmobs;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link MythicMobsIntegrator} instances.
 */
public class MythicMobsIntegratorFactory implements IntegratorFactory {

    /**
     * Creates a new instance of the factory.
     */
    public MythicMobsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new MythicMobsIntegrator();
    }
}
