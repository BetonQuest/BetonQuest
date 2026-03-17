package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.compatibility.nexo.item.NexoItemFactory;
import org.betonquest.betonquest.compatibility.nexo.item.NexoQuestItemSerializer;

/**
 * Handles integration with Nexo.
 */
public class NexoIntegrator implements Integration {

    /**
     * The empty default constructor.
     */
    public NexoIntegrator() {
        // Empty
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final ItemRegistry itemRegistry = api.items().registry();
        itemRegistry.register("nexo", new NexoItemFactory());
        itemRegistry.registerSerializer("nexo", new NexoQuestItemSerializer());
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
