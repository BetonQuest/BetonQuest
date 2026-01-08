package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.craftengine.action.CraftEngineSetBlockActionFactory;
import org.betonquest.betonquest.compatibility.craftengine.action.CraftEngineSetFurnitureActionFactory;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineItemFactory;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineQuestItemSerializer;
import org.betonquest.betonquest.item.ItemRegistry;

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
        final QuestTypeRegistries questTypeRegistries = api.getQuestRegistries();

        questTypeRegistries.action().register("craftEngineBlockAt", new CraftEngineSetBlockActionFactory());
        questTypeRegistries.action().register("craftEngineFurnitureAt", new CraftEngineSetFurnitureActionFactory());

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
