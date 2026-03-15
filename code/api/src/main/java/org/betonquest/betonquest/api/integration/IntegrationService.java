package org.betonquest.betonquest.api.integration;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link IntegrationService} provides an instance of the {@link Integrations}.
 */
public interface IntegrationService {

    /**
     * Create a new {@link Integrations} instance to register {@link Integration}s.
     *
     * @param minimalPluginVersion the optional minimal required minecraft version of the server
     * @return a new {@link Integrations} instance
     */
    Integrations integrator(@Nullable String minimalPluginVersion);

    /**
     * Create a new {@link Integrations} instance to register {@link Integration}s.
     *
     * @param pluginName           the name to identify the plugin with
     * @param minimalPluginVersion the optional minimal required version of the plugin
     * @return a new {@link Integrations} instance
     */
    Integrations integrator(String pluginName, @Nullable String minimalPluginVersion);

    /**
     * Create a new {@link Integrations} instance to register {@link Integration}s.
     *
     * @param pluginClass          the class of the plugin
     * @param minimalPluginVersion the optional minimal required version of the plugin
     * @return a new {@link Integrations} instance
     */
    Integrations integrator(Class<? extends Plugin> pluginClass, @Nullable String minimalPluginVersion);

    /**
     * Create a new {@link Integrations} instance to register {@link Integration}s.
     *
     * @param pluginInstance       the plugin instance
     * @param minimalPluginVersion the optional minimal required version of the plugin
     * @return a new {@link Integrations} instance
     */
    Integrations integrator(Plugin pluginInstance, @Nullable String minimalPluginVersion);
}
