package org.betonquest.betonquest.compatibility.fakeblock;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link FakeBlockIntegrator} instances.
 */
public class FakeBlockIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public FakeBlockIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new FakeBlockIntegrator();
    }
}
