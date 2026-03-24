package org.betonquest.betonquest.integration;

import com.google.common.collect.Sets;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationBuilder;
import org.betonquest.betonquest.api.integration.IntegrationPolicy;
import org.betonquest.betonquest.api.integration.policy.Policy;
import org.bukkit.plugin.Plugin;

import java.util.Set;
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
     * All policies for this instance.
     */
    private final Set<Policy> policies;

    /**
     * Creates a new DefaultIntegrations instance.
     *
     * @param manager  the {@link IntegrationManager} instance
     * @param policies the policies to register
     */
    public DefaultIntegrationPolicy(final IntegrationManager manager, final Set<Policy> policies) {
        this.manager = manager;
        this.policies = policies;
    }

    @Override
    public IntegrationPolicy withPolicies(final Policy... policies) {
        final Set<Policy> combinedPolicies = Sets.union(this.policies, Set.of(policies));
        return new DefaultIntegrationPolicy(manager, combinedPolicies);
    }

    @Override
    public IntegrationBuilder builder() {
        return new DefaultIntegrationBuilder(this);
    }

    @Override
    public void register(final Plugin integratingPlugin, final Supplier<Integration> integration) {
        manager.register(integration, integratingPlugin, policies);
    }
}
