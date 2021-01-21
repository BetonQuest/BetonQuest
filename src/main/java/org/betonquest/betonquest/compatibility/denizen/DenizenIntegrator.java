package org.betonquest.betonquest.compatibility.denizen;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
public class DenizenIntegrator implements Integrator {

    private final BetonQuest plugin;

    public DenizenIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerEvents("script", DenizenTaskScriptEvent.class);
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
