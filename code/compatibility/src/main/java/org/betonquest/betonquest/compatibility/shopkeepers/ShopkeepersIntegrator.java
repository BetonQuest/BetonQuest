package org.betonquest.betonquest.compatibility.shopkeepers;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;

/**
 * Integrator for Shopkeepers.
 */
public class ShopkeepersIntegrator implements Integration {

    /**
     * The minimum required version of Shopkeepers.
     */
    public static final String REQUIRED_VERSION = "2.2.0";

    /**
     * The default constructor.
     */
    public ShopkeepersIntegrator() {

    }

    @Override
    public void enable(final BetonQuestApi api) {
        api.conditions().registry().register("shopamount", new HavingShopConditionFactory());
        api.actions().registry().register("shopkeeper", new OpenShopActionFactory());
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
