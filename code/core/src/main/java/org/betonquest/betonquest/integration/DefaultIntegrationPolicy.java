package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationBuilder;
import org.betonquest.betonquest.api.integration.IntegrationPolicy;
import org.betonquest.betonquest.lib.versioning.Version;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The default implementation of {@link IntegrationPolicy}.
 */
public class DefaultIntegrationPolicy implements IntegrationPolicy {

    /**
     * The {@link IntegrationManager} instance.
     */
    private final IntegrationManager manager;

    /**
     * The {@link PluginProvider} instance.
     */
    private final PluginProvider pluginProvider;

    /**
     * The optional minimal version of the plugin.
     */
    @Nullable
    private final Version minimalVersion;

    /**
     * Creates a new DefaultIntegrations instance.
     *
     * @param manager        The {@link IntegrationManager} instance.
     * @param pluginProvider The {@link PluginProvider} instance.
     * @param minimalVersion The optional minimal version of the plugin.
     */
    public DefaultIntegrationPolicy(final IntegrationManager manager, final PluginProvider pluginProvider, @Nullable final Version minimalVersion) {
        this.manager = manager;
        this.pluginProvider = pluginProvider;
        this.minimalVersion = minimalVersion;
    }

    @Override
    public IntegrationBuilder builder() {
        return new DefaultIntegrationBuilder(this);
    }

    @Override
    public void register(final Plugin integratingPlugin, final Supplier<Integration> integration) {
        manager.register(integration, pluginProvider, integratingPlugin, minimalVersion);
    }
}
