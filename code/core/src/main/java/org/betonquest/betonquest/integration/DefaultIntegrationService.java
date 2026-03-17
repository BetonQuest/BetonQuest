package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.integration.IntegrationPolicy;
import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.lib.versioning.Version;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link IntegrationService} handling all integrations.
 */
public class DefaultIntegrationService implements IntegrationService {

    /**
     * The {@link IntegrationManager} instance to register integrations with.
     */
    private final IntegrationManager integrationManager;

    /**
     * Creates a new instance of {@link DefaultIntegrationService}.
     *
     * @param integrationManager the {@link IntegrationManager} instance
     */
    public DefaultIntegrationService(final IntegrationManager integrationManager) {
        this.integrationManager = integrationManager;
    }

    @Override
    public IntegrationPolicy withPolicy(@Nullable final String minimalMinecraftVersion) {
        final Version version = minimalMinecraftVersion == null ? null : new Version(minimalMinecraftVersion);
        return new DefaultIntegrationPolicy(integrationManager, org.betonquest.betonquest.lib.integration.PluginProvider.EMPTY, version);
    }

    @Override
    public IntegrationPolicy withPolicy(final String pluginName, @Nullable final String minimalPluginVersion) {
        final Version version = minimalPluginVersion == null ? null : new Version(minimalPluginVersion);
        return new DefaultIntegrationPolicy(integrationManager, org.betonquest.betonquest.lib.integration.PluginProvider.forName(pluginName), version);
    }

    @Override
    public IntegrationPolicy withPolicy(final Class<? extends Plugin> pluginClass, @Nullable final String minimalPluginVersion) {
        final Version version = minimalPluginVersion == null ? null : new Version(minimalPluginVersion);
        return new DefaultIntegrationPolicy(integrationManager, org.betonquest.betonquest.lib.integration.PluginProvider.forClass(pluginClass), version);
    }

    @Override
    public IntegrationPolicy withPolicy(final Plugin integratedPlugin, @Nullable final String minimalPluginVersion) {
        final Version version = minimalPluginVersion == null ? null : new Version(minimalPluginVersion);
        return new DefaultIntegrationPolicy(integrationManager, org.betonquest.betonquest.lib.integration.PluginProvider.forInstance(integratedPlugin), version);
    }
}
