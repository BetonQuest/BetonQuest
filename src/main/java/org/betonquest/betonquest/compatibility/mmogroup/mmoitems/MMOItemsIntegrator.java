package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.take.MMOItemsTakeEventFactory;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;

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
        final ConditionTypeRegistry conditionTypes = questRegistries.getConditionTypes();
        conditionTypes.register("mmoitem", MMOItemsItemCondition.class);
        conditionTypes.register("mmohand", MMOItemsHandCondition.class);

        plugin.registerObjectives("mmoitemcraft", MMOItemsCraftObjective.class);
        plugin.registerObjectives("mmoitemupgrade", MMOItemsUpgradeObjective.class);
        plugin.registerObjectives("mmoitemapplygem", MMOItemsApplyGemObjective.class);

        final EventTypeRegistry eventTypes = questRegistries.getEventTypes();
        eventTypes.register("mmoitemgive", MMOItemsGiveEvent.class);
        eventTypes.register("mmoitemtake", new MMOItemsTakeEventFactory(plugin.getLoggerFactory()));
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
