package org.betonquest.betonquest.api.integration;

/**
 * The {@link Integrations} interface allows to register {@link Integration}s.
 */
public interface Integrations {

    /**
     * Creates a new {@link IntegrationBuilder} to register an integration.
     *
     * @return a new {@link IntegrationBuilder}
     */
    IntegrationBuilder builder();

    /**
     * Registers an integration.
     *
     * @param integration the integration to register
     */
    void register(Integration integration);
}
