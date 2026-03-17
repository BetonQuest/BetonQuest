package org.betonquest.betonquest.compatibility.denizen;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.compatibility.denizen.action.DenizenTaskScriptActionFactory;

/**
 * Integrator for Denizen.
 */
public class DenizenIntegrator implements Integration {

    /**
     * The default constructor.
     */
    public DenizenIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        api.actions().registry().register("script", new DenizenTaskScriptActionFactory());
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
