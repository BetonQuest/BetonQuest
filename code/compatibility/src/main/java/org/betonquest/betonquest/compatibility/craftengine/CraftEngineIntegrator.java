package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineItemFactory;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineQuestItemSerializer;
import org.betonquest.betonquest.item.ItemRegistry;

public class CraftEngineIntegrator implements Integrator {

    @Override
    public void hook(final BetonQuestApi api) throws HookException {

        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register("craftEngine", new CraftEngineItemFactory());
        itemRegistry.registerSerializer("craftEngine", new CraftEngineQuestItemSerializer());
    }

    @Override
    public void reload() {}

    @Override
    public void close() {}
}
