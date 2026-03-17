package org.betonquest.betonquest.api.integration;

import org.bukkit.plugin.Plugin;

import java.util.function.Supplier;

/**
 * The {@link IntegrationPolicy} interface allows to register {@link Integration}s.
 */
public interface IntegrationPolicy {

    /**
     * Creates a new {@link IntegrationBuilder} to register an integration.
     *
     * @return a new {@link IntegrationBuilder}
     */
    IntegrationBuilder builder();

    /**
     * Registers an integration.
     *
     * @param integratingPlugin the plugin registering the integration
     * @param integration       the supplier providing the integration to register
     */
    void register(Plugin integratingPlugin, Supplier<Integration> integration);
}
