package org.betonquest.betonquest.compatibility.nexo

import org.betonquest.betonquest.api.BetonQuestApi
import org.betonquest.betonquest.compatibility.Integrator

/**
 * Integrator for Nexo.
 */
class NexoIntegrator : Integrator {
    override fun hook(api: BetonQuestApi?) {

    }

    override fun reload() {
        // Nothing to reload
    }

    override fun close() {
        // Nothing to close
    }
}
