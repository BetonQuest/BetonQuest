package pl.betoncraft.betonquest.compatibility.playerpoints;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class PlayerPointsIntegrator implements Integrator {

    private BetonQuest plugin;

    public PlayerPointsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerEvents("playerpoints", PlayerPointsEvent.class);
        plugin.registerConditions("playerpoints", PlayerPointsCondition.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
