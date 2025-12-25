package org.betonquest.betonquest.compatibility;

/**
 * Factory for creating {@link Integrator} instances.
 */
@FunctionalInterface
public interface IntegratorFactory {

    /**
     * Creates a new {@link Integrator} instance.
     *
     * @return a new {@link Integrator} instance
     */
    Integrator getIntegrator();
}
