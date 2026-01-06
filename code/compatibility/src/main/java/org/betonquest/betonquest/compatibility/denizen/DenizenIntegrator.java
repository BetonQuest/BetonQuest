package org.betonquest.betonquest.compatibility.denizen;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.denizen.action.DenizenTaskScriptActionFactory;

/**
 * Integrator for Denizen.
 */
public class DenizenIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public DenizenIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        api.getQuestRegistries().action().register("script", new DenizenTaskScriptActionFactory());
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
