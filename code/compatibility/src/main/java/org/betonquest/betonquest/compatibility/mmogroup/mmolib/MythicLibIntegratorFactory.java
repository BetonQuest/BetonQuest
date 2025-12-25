package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link MythicLibIntegrator} instances.
 */
public class MythicLibIntegratorFactory implements IntegratorFactory {

    /**
     * Creates a new instance of the factory.
     */
    public MythicLibIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new MythicLibIntegrator();
    }
}
