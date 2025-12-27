package org.betonquest.betonquest.compatibility;

import java.util.List;

/**
 * Holds all possible integrations for a single plugin.
 */
@FunctionalInterface
public interface IntegrationSource {

    /**
     * Gets a list of registered integrations.
     *
     * @return immutable list of data
     */
    List<IntegrationData> getDataList();
}
