package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsApplyGemObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsUpgradeObjectiveFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for MMO Items.
 */
public class MMOItemsIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of MMOItems.
     */
    public static final String REQUIRED_VERSION = "6.9.4-SNAPSHOT";

    /**
     * Creates a new Integrator.
     */
    public MMOItemsIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        objective("upgrade", new MMOItemsUpgradeObjectiveFactory());
        objective("applygem", new MMOItemsApplyGemObjectiveFactory());

        item("", new MMOQuestItemFactory(MMOItems.plugin), new MMOQuestItemSerializer());

        registerFeatures(api, "mmoitem");

        api.bukkit().registerEvents(new MMOItemsCraftObjectiveAdder(api.profiles()));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
