package org.betonquest.betonquest.compatibility.jobsreborn;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link JobsRebornIntegrator} instances.
 */
public class JobsRebornIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public JobsRebornIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new JobsRebornIntegrator();
    }
}
