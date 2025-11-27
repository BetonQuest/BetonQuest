package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.nexo.item.NexoItemFactory;
import org.betonquest.betonquest.compatibility.nexo.item.NexoQuestItemSerializer;
import org.betonquest.betonquest.item.ItemRegistry;

/**
 * Integrator for Nexo.
 */
public class NexoIntegrator implements Integrator {

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();

        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register("nexo", new NexoItemFactory());
        itemRegistry.registerSerializer("nexo", new NexoQuestItemSerializer());
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }
}
