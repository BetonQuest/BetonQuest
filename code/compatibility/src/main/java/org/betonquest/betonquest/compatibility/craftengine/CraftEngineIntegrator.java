package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.item.ItemRegistry;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineItemFactory;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineQuestItemSerializer;

/**
 * Integrator for CraftEngine.
 */
public class CraftEngineIntegrator implements Integrator {

    /**
     * The empty default constructor.
     */
    public CraftEngineIntegrator() {
        // Empty
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register("craftEngine", new CraftEngineItemFactory());
        itemRegistry.registerSerializer("craftEngine", new CraftEngineQuestItemSerializer());
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
