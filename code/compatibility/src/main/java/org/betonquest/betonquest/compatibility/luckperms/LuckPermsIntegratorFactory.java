package org.betonquest.betonquest.compatibility.luckperms;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link LuckPermsIntegrator} instances.
 */
public class LuckPermsIntegratorFactory implements IntegratorFactory {

    /**
     * Creates a new instance of the factory.
     */
    public LuckPermsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new LuckPermsIntegrator();
    }
}
