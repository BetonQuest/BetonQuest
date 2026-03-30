package org.betonquest.betonquest.lib.integration;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.api.version.VersionType;
import org.betonquest.betonquest.lib.version.DefaultVersionType;
import org.betonquest.betonquest.lib.version.VersionParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * Provides access to a plugin instance and its metadata.
 * <p>
 * This interface serves as a functional interface for retrieving plugin instances
 * in a uniform way, whether the plugin is loaded by name, class, or direct instance.
 * It also provides convenient methods to access common plugin metadata such as version and name.
 */
@FunctionalInterface
public interface PluginProvider {

    /**
     * An empty {@link PluginProvider} instance that always returns an empty {@link Optional}.
     * <p>
     * This can be used as a null-object pattern to avoid null checks when no plugin is available.
     */
    PluginProvider EMPTY = Optional::empty;

    /**
     * Creates a new {@link PluginProvider} instance that wraps the given plugin instance.
     * <p>
     * This factory method is useful when you already have a plugin instance and want to
     * provide it through the {@link PluginProvider} interface.
     *
     * @param plugin the plugin instance to wrap
     * @return a new {@link PluginProvider} instance that returns the given plugin
     */
    @Contract(pure = true, value = "_ -> new")
    static PluginProvider forInstance(final Plugin plugin) {
        return () -> Optional.of(plugin);
    }

    /**
     * Creates a new {@link PluginProvider} instance that looks up a plugin by its name.
     * <p>
     * This factory method queries the Bukkit {@link PluginManager} to find a plugin with the specified name.
     * The lookup is performed each time {@link #plugin()} is called, allowing for dynamic plugin loading.
     *
     * @param pluginName the name of the plugin to look up
     * @return a new {@link PluginProvider} instance that returns the plugin with the given name,
     * or empty if the plugin is not found
     */
    @Contract(pure = true, value = "_ -> new")
    static PluginProvider forName(final String pluginName) {
        return () -> Optional.ofNullable(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    /**
     * Returns the plugin instance if it is available.
     * <p>
     * This is the core method of the {@link PluginProvider} interface. The returned
     * {@link Optional} will contain the plugin instance if it exists and is enabled,
     * or will be empty otherwise.
     *
     * @return an {@link Optional} containing the plugin instance if present, empty otherwise
     */
    Optional<Plugin> plugin();

    /**
     * Returns the plugin's version as a {@link Version} object.
     * <p>
     * This method extracts the version string from the plugin's description and parses it
     * into a {@link Version} object for easier version comparison.
     *
     * @return an {@link Optional} containing the plugin version if the plugin is present,
     * empty otherwise
     */
    default Optional<Version> version() {
        return plugin().map(plugin -> VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, plugin.getDescription().getVersion()));
    }

    /**
     * Returns the plugin's name as defined in its plugin.yml.
     * <p>
     * This method provides convenient access to the plugin's name without needing
     * to manually extract it from the plugin's description.
     *
     * @return an {@link Optional} containing the plugin's name if the plugin is present,
     * empty otherwise
     */
    default Optional<String> name() {
        return plugin().map(Plugin::getName);
    }

    /**
     * Returns a new {@link PluginProvider} instance that uses the given {@link DefaultVersionType}
     * to parse the plugins version.
     *
     * @param versionType the version type to use for parsing the version of the plugin
     * @return a new {@link PluginProvider} instance that uses the given version type
     */
    @Contract(pure = true, value = "_ -> new")
    default PluginProvider withVersionType(final VersionType versionType) {
        return new PluginProvider() {
            @Override
            public Optional<Plugin> plugin() {
                return PluginProvider.this.plugin();
            }

            @Override
            public Optional<Version> version() {
                return plugin().map(plugin -> VersionParser.parse(versionType, plugin.getDescription().getVersion()));
            }
        };
    }
}
