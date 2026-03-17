package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineItemFactory;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineQuestItemSerializer;

/**
 * Integrator for CraftEngine.
 */
public class CraftEngineIntegrator implements Integration {

    /**
     * The empty default constructor.
     */
    public CraftEngineIntegrator() {
        // Empty
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final ItemRegistry itemRegistry = api.items().registry();
        itemRegistry.register("craftEngine", new CraftEngineItemFactory());
        itemRegistry.registerSerializer("craftEngine", new CraftEngineQuestItemSerializer());
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
