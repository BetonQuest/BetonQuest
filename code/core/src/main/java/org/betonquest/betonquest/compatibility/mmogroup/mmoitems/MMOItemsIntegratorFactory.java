package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link MMOItemsIntegrator} instances.
 */
public class MMOItemsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public MMOItemsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new MMOItemsIntegrator();
    }
}
