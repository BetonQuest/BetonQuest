package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.api.integration.Integrations;
import org.betonquest.betonquest.lib.versioning.Version;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link IntegrationService} handling all integrations.
 */
public class DefaultIntegrationService implements IntegrationService {

    @Override
    public Integrations integrator(final String pluginName, @Nullable final String minimalPluginVersion) {
        final Version version = minimalPluginVersion == null ? null : new Version(minimalPluginVersion);
        return new DefaultIntegrations(this, PluginProvider.of(pluginName), version);
    }

    @Override
    public Integrations integrator(final Class<? extends Plugin> pluginClass, @Nullable final String minimalPluginVersion) {
        final Version version = minimalPluginVersion == null ? null : new Version(minimalPluginVersion);
        return new DefaultIntegrations(this, PluginProvider.of(pluginClass), version);
    }

    @Override
    public Integrations integrator(final Plugin pluginInstance, @Nullable final String minimalPluginVersion) {
        final Version version = minimalPluginVersion == null ? null : new Version(minimalPluginVersion);
        return new DefaultIntegrations(this, PluginProvider.of(pluginInstance), version);
    }
}
