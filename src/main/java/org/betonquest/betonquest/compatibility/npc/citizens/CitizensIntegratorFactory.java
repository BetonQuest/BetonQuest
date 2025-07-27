package org.betonquest.betonquest.compatibility.npc.citizens;

import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link CitizensIntegrator} instances.
 */
public class CitizensIntegratorFactory implements IntegratorFactory {
    /**
     * The compatibility instance to use for checking other hooks.
     */
    private final Compatibility compatibility;

    /**
     * Creates a new Citizens integrator factory.
     *
     * @param compatibility the compatibility instance to use for checking other hooks
     */
    public CitizensIntegratorFactory(final Compatibility compatibility) {
        this.compatibility = compatibility;
    }

    @Override
    public Integrator getIntegrator() {
        return new CitizensIntegrator(compatibility);
    }
}
