package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.lib.integration.PluginProvider;
import org.bukkit.plugin.Plugin;

/**
 * A record that represents a versioned plugin policy, which validates the compatibility
 * of a plugin based on its presence and version.
 * <p>
 * This class implements the {@link VersionedPolicy} interface and provides a mechanism
 * for ensuring that a specific plugin meets the required version constraints.
 * The validation logic also ensures that the provided plugin exists and is enabled.
 *
 * @param pluginProvider         the {@link PluginProvider} which supplies the plugin instance to validate
 * @param version                the {@link Version} object of the required version that the plugin must satisfy
 * @param versionCompareStrategy the strategy for comparing the plugin's actual version with the required version
 * @param description            the description of the policy used for logging or debugging purposes
 */
public record VersionedPluginPolicy(PluginProvider pluginProvider, Version version,
                                    VersionCompareStrategy versionCompareStrategy,
                                    String description) implements VersionedPolicy, PluginPolicy {

    @Override
    public boolean validate() {
        return pluginProvider.plugin().map(Plugin::isEnabled).orElse(false)
                && pluginProvider.withVersionType(version.type()).version()
                .map(actual -> versionCompareStrategy.test(actual, version)).orElse(false);
    }

    @Override
    public String name() {
        return pluginProvider.name().orElse("unknown plugin");
    }
}
