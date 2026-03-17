package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.integration.IntegrationPolicy;
import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.api.integration.policy.Policy;

import java.util.Set;

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
    public IntegrationPolicy withPolicies(final Policy... policies) {
        return new DefaultIntegrationPolicy(integrationManager, Set.of(policies));
    }
}
