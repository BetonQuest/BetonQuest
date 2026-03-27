package org.betonquest.betonquest.compatibility.shopkeepers;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for Shopkeepers.
 */
public class ShopkeepersIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of Shopkeepers.
     */
    public static final String REQUIRED_VERSION = "2.2.0";

    /**
     * The default constructor.
     */
    public ShopkeepersIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        playerCondition("amount", new HavingShopConditionFactory());
        playerAction("keeper", new OpenShopActionFactory());

        registerFeatures(api, "shop");
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
