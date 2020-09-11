package pl.betoncraft.betonquest.compatibility.denizen;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class DenizenIntegrator implements Integrator {

    private BetonQuest plugin;

    public DenizenIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerEvents("script", DenizenTaskScriptEvent.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
