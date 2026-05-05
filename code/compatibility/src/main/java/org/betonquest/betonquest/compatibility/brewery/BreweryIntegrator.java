package org.betonquest.betonquest.compatibility.brewery;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.condition.DrunkQualityConditionFactory;
import org.betonquest.betonquest.compatibility.brewery.item.BrewItemFactory;
import org.betonquest.betonquest.compatibility.brewery.item.BrewQuestItemSerializer;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for the Brewery plugin.
 */
public class BreweryIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of Brewery.
     */
    public static final String REQUIRED_VERSION = "3.1.1";

    /**
     * Create a new Brewery Integrator.
     */
    public BreweryIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        playerCondition("drunk", new DrunkConditionFactory());
        playerCondition("drunkquality", new DrunkQualityConditionFactory());

        item("brew", new BrewItemFactory(), new BrewQuestItemSerializer());

        registerFeatures(api);
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
