package org.betonquest.betonquest.compatibility.vault;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link VaultIntegrator} instances.
 */
public class VaultIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public VaultIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new VaultIntegrator();
    }
}
