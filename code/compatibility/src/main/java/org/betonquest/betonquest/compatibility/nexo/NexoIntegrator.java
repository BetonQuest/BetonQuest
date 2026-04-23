package org.betonquest.betonquest.compatibility.nexo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.nexo.item.NexoItemFactory;
import org.betonquest.betonquest.compatibility.nexo.item.NexoQuestItemSerializer;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Handles integration with Nexo.
 */
public class NexoIntegrator extends IntegrationTemplate {

    /**
     * The minimum required version of Nexo.
     */
    public static final String REQUIRED_VERSION = "1.17.0";

    /**
     * The empty default constructor.
     */
    public NexoIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        item("nexo", new NexoItemFactory(), new NexoQuestItemSerializer());

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
