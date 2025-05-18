package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsApplyGemObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsUpgradeObjectiveFactory;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;

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
    public void hook() {
        final BetonQuest plugin = BetonQuest.getInstance();
        final ObjectiveTypeRegistry objectiveTypes = plugin.getQuestRegistries().objective();
        objectiveTypes.register("mmoitemupgrade", new MMOItemsUpgradeObjectiveFactory());
        objectiveTypes.register("mmoitemapplygem", new MMOItemsApplyGemObjectiveFactory());

        final ItemTypeRegistry itemTypes = plugin.getFeatureRegistries().item();
        itemTypes.register("mmoitem", new MMOQuestItemFactory(MMOItems.plugin));
        itemTypes.registerSerializer("mmoitem", new MMOQuestItemSerializer());
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
