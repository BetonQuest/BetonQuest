package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link MMOCoreIntegrator} instances.
 */
public class MMOCoreIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public MMOCoreIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new MMOCoreIntegrator();
    }
}
