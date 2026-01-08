package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.nexo.action.NexoSetBlockActionFactory;
import org.betonquest.betonquest.compatibility.nexo.action.NexoSetFurnitureActionFactory;
import org.betonquest.betonquest.compatibility.nexo.item.NexoItemFactory;
import org.betonquest.betonquest.compatibility.nexo.item.NexoQuestItemSerializer;
import org.betonquest.betonquest.item.ItemRegistry;

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
    public void hook(final BetonQuestApi api) throws HookException {
        final QuestTypeRegistries questTypeRegistries = api.getQuestRegistries();

        questTypeRegistries.action().register("nexoBlockAt", new NexoSetBlockActionFactory());
        questTypeRegistries.action().register("nexoFurnitureAt", new NexoSetFurnitureActionFactory());

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
