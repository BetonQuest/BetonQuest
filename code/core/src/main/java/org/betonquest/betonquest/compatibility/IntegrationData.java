package org.betonquest.betonquest.compatibility;

import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.integration.Integration;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Data of hooking into Plugins or Minecraft versions.
 */
public interface IntegrationData {

    /**
     * Gets the plugin that provides the integration.
     *
     * @return the plugin that registered the integration
     */
    Plugin integrationProvider();

    /**
     * Gets the integration of the data.
     *
     * @return the created integration
     */
    Integration getIntegration();

    /**
     * Gets the integration's hook information.
     * The first value is the name,
     * the second is the version to display,
     * and the third is the extended description.
     *
     * @return the information
     */
    List<Triple<String, String, String>> getDisplayInfo();
}
