package org.betonquest.betonquest.compatibility.shopkeepers;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link ShopkeepersIntegrator} instances.
 */
public class ShopkeepersIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public ShopkeepersIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new ShopkeepersIntegrator();
    }
}
