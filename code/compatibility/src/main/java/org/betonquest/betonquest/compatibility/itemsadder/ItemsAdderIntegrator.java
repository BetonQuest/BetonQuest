package org.betonquest.betonquest.compatibility.itemsadder;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderItemFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderQuestItemSerializer;
import org.betonquest.betonquest.item.ItemRegistry;

/**
 * Handles integration with ItemsAdder.
 */
public class ItemsAdderIntegrator implements Integrator {

    /** The empty default constructor. */
    public ItemsAdderIntegrator() { }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register("itemsAdder", new ItemsAdderItemFactory());
        itemRegistry.registerSerializer("itemsAdder", new ItemsAdderQuestItemSerializer());
    }

    @Override
    public void reload() {}

    @Override
    public void close() {}
}
