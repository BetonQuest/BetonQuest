package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationBuilder;
import org.betonquest.betonquest.api.integration.Integrations;
import org.betonquest.betonquest.lib.versioning.Version;
import org.jetbrains.annotations.Nullable;

/**
 * The default implementation of {@link Integrations}.
 */
public class DefaultIntegrations implements Integrations {

    /**
     * The {@link DefaultIntegrationService} instance.
     */
    private final DefaultIntegrationService service;

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
     * @param service        The {@link DefaultIntegrationService} instance.
     * @param pluginProvider The {@link PluginProvider} instance.
     * @param minimalVersion The optional minimal version of the plugin.
     */
    public DefaultIntegrations(final DefaultIntegrationService service, final PluginProvider pluginProvider, @Nullable final Version minimalVersion) {
        this.service = service;
        this.pluginProvider = pluginProvider;
        this.minimalVersion = minimalVersion;
    }

    @Override
    public IntegrationBuilder builder() {
        return new DefaultIntegrationBuilder(this);
    }

    @Override
    public void register(final Integration integration) {
        //TODO registration logic
    }
}
