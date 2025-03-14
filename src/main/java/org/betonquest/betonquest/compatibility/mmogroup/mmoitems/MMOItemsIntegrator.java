package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.take.MMOItemsTakeEventFactory;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;

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
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("mmoitem", MMOItemsItemCondition.class);
        conditionTypes.register("mmohand", MMOItemsHandCondition.class);

        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("mmoitemcraft", MMOItemsCraftObjective.class);
        objectiveTypes.register("mmoitemupgrade", MMOItemsUpgradeObjective.class);
        objectiveTypes.register("mmoitemapplygem", MMOItemsApplyGemObjective.class);

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("mmoitemgive", MMOItemsGiveEvent.class);
        eventTypes.register("mmoitemtake", new MMOItemsTakeEventFactory(plugin.getLoggerFactory(),
                plugin.getPluginMessage()));
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
