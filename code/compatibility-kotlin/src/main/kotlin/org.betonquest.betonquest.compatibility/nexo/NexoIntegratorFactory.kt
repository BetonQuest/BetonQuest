package org.betonquest.betonquest.compatibility.nexo

import org.betonquest.betonquest.compatibility.Integrator
import org.betonquest.betonquest.compatibility.IntegratorFactory

/**
 * Factory for creating {@link NexoIntegrator} instances.
 */
class NexoIntegratorFactory : IntegratorFactory {

    /**
     * Creates a new instance of the factory.
     */
    override fun getIntegrator(): Integrator = NexoIntegrator()
}
