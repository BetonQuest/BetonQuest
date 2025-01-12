package org.betonquest.betonquest.compatibility.denizen;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

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
    public void hook() {
        BetonQuest.getInstance().getQuestRegistries().getEventTypes().register("script", DenizenTaskScriptEvent.class);
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
