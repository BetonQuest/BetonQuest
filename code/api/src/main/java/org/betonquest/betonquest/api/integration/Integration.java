package org.betonquest.betonquest.api.integration;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;

/**
 * Represents a set of features to be integrated with BetonQuest.
 */
public interface Integration {

    /**
     * Enables the integration.
     *
     * @param betonQuestApi the BetonQuest API
     * @throws QuestException if the integration fails
     */
    void enable(BetonQuestApi betonQuestApi) throws QuestException;

    /**
     * Enables all parts of the integration that are required to load after all plugins have been enabled.
     * Will run in the first tick after all plugins have been enabled.
     *
     * @param betonQuestApi the BetonQuest API
     * @throws QuestException if the integration fails
     */
    void postEnable(BetonQuestApi betonQuestApi) throws QuestException;

    /**
     * Disables the integration.
     *
     * @throws QuestException if the integration teardown fails
     */
    void disable() throws QuestException;
}
