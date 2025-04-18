package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.condition.MMOItemsHandConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.condition.MMOItemsItemConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.event.MMOItemsGiveEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.event.MMOItemsTakeEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsApplyGemObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsCraftObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsUpgradeObjectiveFactory;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

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
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("mmoitem", new MMOItemsItemConditionFactory(loggerFactory, plugin.getPlayerDataStorage(), data));
        conditionTypes.register("mmohand", new MMOItemsHandConditionFactory(loggerFactory, data));

        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("mmoitemcraft", new MMOItemsCraftObjectiveFactory());
        objectiveTypes.register("mmoitemupgrade", new MMOItemsUpgradeObjectiveFactory());
        objectiveTypes.register("mmoitemapplygem", new MMOItemsApplyGemObjectiveFactory());

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("mmoitemgive", new MMOItemsGiveEventFactory(loggerFactory, plugin.getPluginMessage(), data));
        eventTypes.register("mmoitemtake", new MMOItemsTakeEventFactory(loggerFactory, plugin.getPluginMessage()));
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
