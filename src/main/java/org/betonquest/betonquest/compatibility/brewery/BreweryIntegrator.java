package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class BreweryIntegrator implements Integrator {

    private final BetonQuest plugin;

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
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
