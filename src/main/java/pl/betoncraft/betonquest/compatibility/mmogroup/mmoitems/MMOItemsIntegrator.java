package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;

public class MMOItemsIntegrator implements Integrator {

    @Override
    public void hook() {
        BetonQuest plugin = BetonQuest.getInstance();
        plugin.registerConditions("mmoitem", MMOItemsItemCondition.class);
        plugin.registerConditions("mmohand", MMOItemsHandCondition.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }
}
