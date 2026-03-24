package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.lib.integration.PluginProvider;
import org.bukkit.plugin.Plugin;

/**
 * A record that represents an unversioned plugin policy, which validates the compatibility
 * of a plugin based solely on its presence.
 * <p>
 * The validation logic ensures that the provided plugin exists and is enabled.
 *
 * @param pluginProvider the {@link PluginProvider} which supplies the plugin instance to validate
 * @param description    the description of the policy used for logging or debugging purposes
 */
public record UnversionedPluginPolicy(PluginProvider pluginProvider, String description) implements PluginPolicy {

    @Override
    public boolean validate() {
        return pluginProvider.plugin().map(Plugin::isEnabled).orElse(false);
    }
}
