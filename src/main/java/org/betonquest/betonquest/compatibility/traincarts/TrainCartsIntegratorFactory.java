package org.betonquest.betonquest.compatibility.traincarts;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link TrainCartsIntegrator} instances.
 */
public class TrainCartsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public TrainCartsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new TrainCartsIntegrator();
    }
}
