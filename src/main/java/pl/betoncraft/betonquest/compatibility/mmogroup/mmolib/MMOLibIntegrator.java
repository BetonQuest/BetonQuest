package pl.betoncraft.betonquest.compatibility.mmogroup.mmolib;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;

public class MMOLibIntegrator implements Integrator {

    @Override
    public void hook() {
        final BetonQuest plugin = BetonQuest.getInstance();
        plugin.registerConditions("mmostat", MMOLibStatCondition.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
