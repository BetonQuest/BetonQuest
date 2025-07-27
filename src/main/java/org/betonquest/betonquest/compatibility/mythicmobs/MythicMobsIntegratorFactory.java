package org.betonquest.betonquest.compatibility.mythicmobs;

import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link MythicMobsIntegrator} instances.
 */
public class MythicMobsIntegratorFactory implements IntegratorFactory {
    /**
     * The compatibility instance to use.
     */
    private final Compatibility compatibility;

    /**
     * Creates a new instance of the factory.
     *
     * @param compatibility the compatibility instance to use
     */
    public MythicMobsIntegratorFactory(final Compatibility compatibility) {
        this.compatibility = compatibility;
    }

    @Override
    public Integrator getIntegrator() {
        return new MythicMobsIntegrator(compatibility);
    }
}
