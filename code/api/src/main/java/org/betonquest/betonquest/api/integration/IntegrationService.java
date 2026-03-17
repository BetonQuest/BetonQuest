package org.betonquest.betonquest.api.integration;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link IntegrationService} provides an instance of the {@link IntegrationPolicy}.
 * <p>
 * This service offers four overloaded {@code integrator} methods to create {@link IntegrationPolicy} instances
 * with different version requirement strategies:
 * <ul>
 *     <li>{@link #withPolicy(String)} - Validates against the server's Minecraft version</li>
 *     <li>{@link #withPolicy(String, String)} - Validates against a plugin identified by name</li>
 *     <li>{@link #withPolicy(Class, String)} - Validates against a plugin identified by class</li>
 *     <li>{@link #withPolicy(Plugin, String)} - Validates against a specific plugin instance</li>
 * </ul>
 * All methods accept an optional minimal version requirement. If the actual version is lower than the
 * specified version requirement, integrations registered through the returned {@link IntegrationPolicy} instance will
 * not be enabled. This prevents collisions between different versions of the integrated plugin.
 */
public interface IntegrationService {

    /**
     * Creates a new {@link IntegrationPolicy} instance to register {@link Integration}s with version validation
     * against the server's Minecraft version.
     * <p>
     * If a minimal version requirement is specified and the server's Minecraft version is lower than the
     * required version, integrations registered through the returned {@link IntegrationPolicy} instance will
     * not be enabled. This ensures compatibility by preventing integrations from running on unsupported
     * Minecraft versions.
     * <p>
     * Example usage:
     * <pre>{@code
     * // Require Minecraft 1.20 or higher
     * Integrations integrations = service.integrator("1.20");
     *
     * // No version requirement
     * Integrations integrations = service.integrator(null);
     * }</pre>
     *
     * @param minimalMinecraftVersion the optional minimal required Minecraft version of the server,
     *                                or {@code null} to disable version validation
     * @return a new {@link IntegrationPolicy} instance for registering integrations
     * @see #withPolicy(String, String)
     * @see #withPolicy(Class, String)
     * @see #withPolicy(Plugin, String)
     */
    IntegrationPolicy withPolicy(@Nullable String minimalMinecraftVersion);

    /**
     * Creates a new {@link IntegrationPolicy} instance to register {@link Integration}s with version validation
     * against a plugin identified by name.
     * <p>
     * If a minimal version requirement is specified and the identified plugin's version is lower than the
     * required version, integrations registered through the returned {@link IntegrationPolicy} instance will
     * not be enabled. This ensures compatibility by preventing integrations from running with unsupported
     * plugin versions.
     * <p>
     * Specifying a plugin ensures the integration will only be enabled if the plugin is enabled on the server.
     * <p>
     * <b>Important:</b> Plugin names are not guaranteed to be unique. If the integrated plugin has a
     * common or generic name that might conflict with other plugins, consider using
     * {@link #withPolicy(Class, String)} or {@link #withPolicy(Plugin, String)} for more reliable
     * plugin identification.
     * <p>
     * Example usage:
     * <pre>{@code
     * // Require MyPlugin version 2.0 or higher
     * Integrations integrations = service.integrator("MyPlugin", "2.0");
     *
     * // No version requirement, but defines the dependency on MyPlugin
     * Integrations integrations = service.integrator("MyPlugin", null);
     * }</pre>
     *
     * @param pluginName           the name of the plugin to identify and validate against version requirements
     * @param minimalPluginVersion the optional minimal required version of the plugin,
     *                             or {@code null} to disable version validation
     * @return a new {@link IntegrationPolicy} instance for registering integrations
     * @see #withPolicy(String)
     * @see #withPolicy(Class, String)
     * @see #withPolicy(Plugin, String)
     */
    IntegrationPolicy withPolicy(String pluginName, @Nullable String minimalPluginVersion);

    /**
     * Creates a new {@link IntegrationPolicy} instance to register {@link Integration}s with version validation
     * against a plugin identified by class.
     * <p>
     * If a minimal version requirement is specified and the identified plugin's version is lower than the
     * required version, integrations registered through the returned {@link IntegrationPolicy} instance will
     * not be enabled. This ensures compatibility by preventing integrations from running with unsupported
     * plugin versions.
     * <p>
     * Specifying a plugin ensures the integration will only be enabled if the plugin is enabled on the server.
     * <p>
     * <b>Recommended:</b> Using a plugin class provides more reliable identification than using a plugin name,
     * as plugin classes are guaranteed to be unique. This prevents conflicts with plugins that might share
     * the same name. This is the preferred method when you have direct access to the plugin's class.
     * <p>
     * Example usage:
     * <pre>{@code
     * // Require MyPlugin version 2.0 or higher
     * Integrations integrations = service.integrator(MyPlugin.class, "2.0");
     *
     * // No version requirement, but defines the dependency on MyPlugin
     * Integrations integrations = service.integrator(MyPlugin.class, null);
     * }</pre>
     *
     * @param pluginClass          the class of the plugin to identify and validate against version requirements
     * @param minimalPluginVersion the optional minimal required version of the plugin,
     *                             or {@code null} to disable version validation
     * @return a new {@link IntegrationPolicy} instance for registering integrations
     * @see #withPolicy(String)
     * @see #withPolicy(String, String)
     * @see #withPolicy(Plugin, String)
     */
    IntegrationPolicy withPolicy(Class<? extends Plugin> pluginClass, @Nullable String minimalPluginVersion);

    /**
     * Creates a new {@link IntegrationPolicy} instance to register {@link Integration}s with version validation
     * against a specific plugin instance.
     * <p>
     * If a minimal version requirement is specified and the plugin's version is lower than the
     * required version, integrations registered through the returned {@link IntegrationPolicy} instance will
     * not be enabled. This ensures compatibility by preventing integrations from running with unsupported
     * plugin versions.
     * <p>
     * Specifying a plugin ensures the integration will only be enabled if the plugin is enabled on the server.
     * <p>
     * <b>Recommended:</b> Using a plugin instance provides the most direct and reliable identification method,
     * as it references the exact plugin object you want to integrate with. This is useful when you already have
     * access to the plugin instance and want to ensure your integration works with that specific instance.
     * <p>
     * Example usage:
     * <pre>{@code
     * // Require MyPlugin version 2.0 or higher
     * Plugin myPlugin = //...
     * Integrations integrations = service.integrator(myPlugin, "2.0");
     *
     * // No version requirement, but defines the dependency on MyPlugin
     * Integrations integrations = service.integrator(myPlugin, null);
     * }</pre>
     *
     * @param integratedPlugin     the plugin instance to identify and validate against version requirements
     * @param minimalPluginVersion the optional minimal required version of the plugin,
     *                             or {@code null} to disable version validation
     * @return a new {@link IntegrationPolicy} instance for registering integrations
     * @see #withPolicy(String)
     * @see #withPolicy(String, String)
     * @see #withPolicy(Class, String)
     */
    IntegrationPolicy withPolicy(Plugin integratedPlugin, @Nullable String minimalPluginVersion);
}
