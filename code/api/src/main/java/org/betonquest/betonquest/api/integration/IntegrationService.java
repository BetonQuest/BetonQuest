package org.betonquest.betonquest.api.integration;

import org.betonquest.betonquest.api.integration.policy.Policy;
import org.jetbrains.annotations.Contract;

/**
 * The {@link IntegrationService} provides an instance of the {@link IntegrationPolicy}.
 * <p>
 * This service offers the method {@link #withPolicies(Policy...)} to create {@link IntegrationPolicy} instances
 * with one, multiple or no {@link Policy}s applied.
 * <p>
 * Policies determine whether integrations should be enabled based on various constraints such as
 * Minecraft version requirements, plugin version requirements, or custom validation logic.
 * Multiple policies can be combined, and all must be satisfied for integrations to be enabled.
 */
@FunctionalInterface
public interface IntegrationService {

    /**
     * Creates a new {@link IntegrationPolicy} instance to register {@link Integration}s with one, multiple or no
     * {@link Policy}s applied.
     * <p>
     * This method allows combining multiple policies to create complex validation rules. All specified
     * policies must be satisfied for integrations registered through the returned {@link IntegrationPolicy}
     * instance to be enabled. This is useful for scenarios requiring multiple constraints, such as
     * validating against both Minecraft version and plugin version requirements.
     * <p>
     * Example usage:
     * <pre>{@code
     * // Require Minecraft 1.20+ and MyPlugin 2.0+
     * Integrations integrations = service.withPolicies(
     *     Policies.minimalVanillaVersion("1.20"),
     *     Policies.minimalPluginVersion("MyPlugin", "2.0")
     * );
     *
     * // Require a version range for a plugin
     * Integrations integrations = service.withPolicies(
     *     Policies.pluginVersionRange("MyPlugin", "2.0", "3.0")
     * );
     *
     * // Require no policies
     * Integrations integrations = service.withPolicies();
     * }</pre>
     *
     * @param policies the {@link Policy} instances to add
     * @return a new {@link IntegrationPolicy} instance for registering integrations
     */
    @Contract(pure = true, value = "_ -> new")
    IntegrationPolicy withPolicies(Policy... policies);
}
