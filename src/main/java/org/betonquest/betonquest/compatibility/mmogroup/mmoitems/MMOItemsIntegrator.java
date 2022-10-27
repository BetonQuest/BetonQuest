package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsIntegrator implements Integrator {

    private final BetonQuest plugin;

    public MMOItemsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("mmoitem", MMOItemsItemCondition.class);
        plugin.registerConditions("mmohand", MMOItemsHandCondition.class);

        plugin.registerObjectives("mmoitemcraft", MMOItemsCraftObjective.class);
        plugin.registerObjectives("mmoitemupgrade", MMOItemsUpgradeObjective.class);
        plugin.registerObjectives("mmoitemapplygem", MMOItemsApplyGemObjective.class);
        plugin.registerObjectives("mmoitemcastability", MMOItemsCastAbilityObjective.class);

        plugin.registerEvents("mmoitemgive", MMOItemsGiveEvent.class);
        plugin.registerEvents("mmoitemtake", MMOItemsTakeEvent.class);
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
