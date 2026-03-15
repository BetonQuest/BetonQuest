package org.betonquest.betonquest.api.integration;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.common.function.QuestRunnable;

/**
 * Offers methods for building integrations in a chain.
 */
public interface IntegrationBuilder {

    /**
     * Registers functionality to be called when the integration is enabled.
     *
     * @param enable the integration to enable
     * @return this
     */
    IntegrationBuilder enable(QuestConsumer<BetonQuestApi> enable);

    /**
     * Registers functionality to be enabled after all plugins are enabled.
     *
     * @param postEnable the integration to enable
     * @return this
     */
    IntegrationBuilder postEnable(QuestConsumer<BetonQuestApi> postEnable);

    /**
     * Registers functionality to be called when the integration is disabled.
     *
     * @param teardown the integration to disable
     * @return this
     */
    IntegrationBuilder teardown(QuestRunnable teardown);

    /**
     * Finalizes the builder by registering the integration with all its methods.
     */
    void integrate();
}
