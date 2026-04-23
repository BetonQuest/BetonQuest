package org.betonquest.betonquest.compatibility.craftengine;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineItemFactory;
import org.betonquest.betonquest.compatibility.craftengine.item.CraftEngineQuestItemSerializer;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for CraftEngine.
 */
public class CraftEngineIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of CraftEngine.
     */
    public static final String REQUIRED_VERSION = "0.0.66";

    /**
     * The empty default constructor.
     */
    public CraftEngineIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        item("craftEngine", new CraftEngineItemFactory(), new CraftEngineQuestItemSerializer());

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
