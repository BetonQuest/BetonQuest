package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsApplyGemObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsUpgradeObjectiveFactory;

/**
 * Integrator for MMO Items.
 */
public class MMOItemsIntegrator implements Integrator {

    /**
     * Creates a new Integrator.
     */
    public MMOItemsIntegrator() {

    }

    @Override
    public void hook(final BetonQuestApi api) {
        final ObjectiveRegistry objectiveRegistry = api.objectives().registry();
        objectiveRegistry.register("mmoitemupgrade", new MMOItemsUpgradeObjectiveFactory());
        objectiveRegistry.register("mmoitemapplygem", new MMOItemsApplyGemObjectiveFactory());

        final ItemRegistry itemRegistry = api.items().registry();
        itemRegistry.register("mmoitem", new MMOQuestItemFactory(MMOItems.plugin));
        itemRegistry.registerSerializer("mmoitem", new MMOQuestItemSerializer());
        api.bukkit().registerEvents(new MMOItemsCraftObjectiveAdder(api.profiles()));
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
