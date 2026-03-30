package org.betonquest.betonquest.api.integration;

import org.betonquest.betonquest.api.integration.policy.Policy;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

/**
 * The {@link IntegrationPolicy} interface allows to register {@link Integration}s.
 */
public interface IntegrationPolicy {

    /**
     * Creates a new policy instance with additional policies combined with the current ones.
     * <p>
     * See {@link IntegrationService#withPolicies(Policy...)} for policy usage details.
     *
     * @param policies additional policies to require
     * @return a new instance with the combined policies
     */
    @Contract(pure = true, value = "_ -> new")
    IntegrationPolicy withPolicies(Policy... policies);

    /**
     * Creates a new {@link IntegrationBuilder} to register an integration.
     *
     * @return a new {@link IntegrationBuilder}
     */
    @Contract(pure = true, value = "-> new")
    IntegrationBuilder builder();

    /**
     * Registers an integration for the given plugin.
     * <p>
     * <b>Important:</b> Avoid using this method with a method reference for the integration supplier.
     * There is a risk of causing unexpected {@link ClassNotFoundException}s when a method reference is used.
     *
     * @param integratingPlugin the plugin registering the integration
     * @param integration       the supplier providing the integration to register
     */
    void register(Plugin integratingPlugin, Supplier<Integration> integration);
}
