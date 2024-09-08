package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.take.MMOItemsTakeEventFactory;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsIntegrator implements Integrator {

    private final BetonQuest plugin;

    public MMOItemsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();

        plugin.registerConditions("mmoitem", MMOItemsItemCondition.class);
        plugin.registerConditions("mmohand", MMOItemsHandCondition.class);

        plugin.registerObjectives("mmoitemcraft", MMOItemsCraftObjective.class);
        plugin.registerObjectives("mmoitemupgrade", MMOItemsUpgradeObjective.class);
        plugin.registerObjectives("mmoitemapplygem", MMOItemsApplyGemObjective.class);

        plugin.registerEvents("mmoitemgive", MMOItemsGiveEvent.class);
        questRegistries.getEventTypes().register("mmoitemtake", new MMOItemsTakeEventFactory(plugin.getLoggerFactory()));
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
