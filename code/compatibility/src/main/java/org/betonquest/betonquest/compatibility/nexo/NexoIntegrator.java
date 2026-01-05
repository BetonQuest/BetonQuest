package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.nexo.item.NexoItemFactory;
import org.betonquest.betonquest.compatibility.nexo.item.NexoQuestItemSerializer;
import org.betonquest.betonquest.item.ItemRegistry;

public class NexoIntegrator implements Integrator {

    @Override
    public void hook(final BetonQuestApi api) throws HookException {

        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register("nexo", new NexoItemFactory());
        itemRegistry.registerSerializer("nexo", new NexoQuestItemSerializer());
    }

    @Override
    public void reload() {}

    @Override
    public void close() {}
}
