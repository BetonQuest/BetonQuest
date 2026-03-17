package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.integration.Integration;
import org.bukkit.plugin.Plugin;

/**
 * Data of hooking into Plugins or Minecraft versions.
 */
public interface IntegrationData {

    /**
     * Gets the plugin that provides the integration.
     *
     * @return the plugin that registered the integration
     */
    Plugin integratorPlugin();

    /**
     * Gets the integration of the data.
     *
     * @return the created integration
     */
    Integration getIntegration();

    /**
     * Gets the target name to display.
     *
     * @return the name of the hooked
     */
    String getName();

    /**
     * Gets the version to show in the compatibility.
     *
     * @return the version string of the hooked
     */
    String getVersion();
}
