package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.lib.versioning.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Optional;

/**
 * Provides a plugin and its meta data.
 */
public interface PluginProvider {

    /**
     * Empty {@link PluginProvider} instance.
     */
    PluginProvider EMPTY = Optional::empty;

    /**
     * Creates a new {@link PluginProvider} instance for the given plugin.
     *
     * @param plugin the plugin instance
     * @return a new {@link PluginProvider} instance for the given plugin
     */
    static PluginProvider of(final Plugin plugin) {
        return () -> Optional.of(plugin);
    }

    /**
     * Creates a new {@link PluginProvider} instance for the given plugin name.
     *
     * @param pluginName the plugin name
     * @return a new {@link PluginProvider} instance for the given plugin name or empty if not found
     */
    static PluginProvider of(final String pluginName) {
        return () -> Optional.ofNullable(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    /**
     * Creates a new {@link PluginProvider} instance for the given plugin class.
     *
     * @param pluginClass the plugin class
     * @return a new {@link PluginProvider} instance for the given plugin class or empty if not found
     */
    static PluginProvider of(final Class<? extends Plugin> pluginClass) {
        return () -> Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(pluginClass::isInstance).findFirst();
    }

    /**
     * Returns the plugin instance if it is present.
     *
     * @return the plugin instance if it is present, empty otherwise
     */
    Optional<Plugin> plugin();

    /**
     * Returns the plugin version as a {@link Version}.
     *
     * @return the plugin version if the plugin is present, empty otherwise
     */
    default Optional<Version> version() {
        return plugin().map(plugin -> new Version(plugin.getDescription().getVersion()));
    }

    /**
     * Returns the plugin's name.
     *
     * @return the plugin's name if the plugin is present, empty otherwise
     */
    default Optional<String> name() {
        return plugin().map(Plugin::getName);
    }
}
