package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsApplyGemObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsUpgradeObjectiveFactory;
import org.betonquest.betonquest.item.ItemRegistry;

/**
 * Integrator for MMO Items.
 */
public class MMOItemsIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public MMOItemsIntegrator() {

    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuest plugin = BetonQuest.getInstance();
        final FeatureTypeRegistry<Objective> objectiveRegistry = plugin.getQuestRegistries().objective();
        objectiveRegistry.register("mmoitemupgrade", new MMOItemsUpgradeObjectiveFactory());
        objectiveRegistry.register("mmoitemapplygem", new MMOItemsApplyGemObjectiveFactory());

        final ItemRegistry itemRegistry = plugin.getFeatureRegistries().item();
        itemRegistry.register("mmoitem", new MMOQuestItemFactory(MMOItems.plugin));
        itemRegistry.registerSerializer("mmoitem", new MMOQuestItemSerializer());
        plugin.getServer().getPluginManager().registerEvents(new MMOItemsCraftObjectiveAdder(plugin.getProfileProvider()), plugin);
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
