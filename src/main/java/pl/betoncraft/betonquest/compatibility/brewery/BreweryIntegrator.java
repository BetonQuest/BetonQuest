package pl.betoncraft.betonquest.compatibility.brewery;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;

public class BreweryIntegrator implements Integrator{

    private BetonQuest plugin;

    public BreweryIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerEvents("givebrew", GiveBrewEvent.class);
        plugin.registerEvents("takebrew", TakeBrewEvent.class);

        plugin.registerConditions("drunk", DrunkCondition.class);
        plugin.registerConditions("drunkquality", DrunkQualityCondition.class);
        plugin.registerConditions("hasbrew", HasBrewCondition.class);

    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }
}
