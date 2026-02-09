package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.item.ItemRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.nexo.item.NexoItemFactory;
import org.betonquest.betonquest.compatibility.nexo.item.NexoQuestItemSerializer;

/**
 * Handles integration with Nexo.
 */
public class NexoIntegrator implements Integrator {

    /**
     * The empty default constructor.
     */
    public NexoIntegrator() {
        // Empty
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register("nexo", new NexoItemFactory());
        itemRegistry.registerSerializer("nexo", new NexoQuestItemSerializer());
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
