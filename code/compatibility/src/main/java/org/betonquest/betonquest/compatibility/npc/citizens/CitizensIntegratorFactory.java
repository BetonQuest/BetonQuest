package org.betonquest.betonquest.compatibility.npc.citizens;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link CitizensIntegrator} instances.
 */
public class CitizensIntegratorFactory implements IntegratorFactory {

    /**
     * Creates a new Citizens integrator factory.
     */
    public CitizensIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new CitizensIntegrator();
    }
}
