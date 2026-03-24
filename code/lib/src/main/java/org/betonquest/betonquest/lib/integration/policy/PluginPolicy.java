package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.lib.integration.PluginProvider;

/**
 * Represents a policy that defines rules in relation to a specific plugin.
 * This interface extends the {@link Policy} interface and includes a method to
 * access the associated {@link PluginProvider}.
 */
public interface PluginPolicy extends Policy {

    /**
     * Retrieves the associated {@link PluginProvider} for this policy.
     *
     * @return the {@link PluginProvider} associated with this policy
     */
    PluginProvider pluginProvider();
}
