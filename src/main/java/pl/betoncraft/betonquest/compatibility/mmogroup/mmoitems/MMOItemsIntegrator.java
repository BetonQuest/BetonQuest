package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;

public class MMOItemsIntegrator implements Integrator {

    public MMOItemsIntegrator() {}
    @Override
    public void hook() {
        final BetonQuest plugin = BetonQuest.getInstance();
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
