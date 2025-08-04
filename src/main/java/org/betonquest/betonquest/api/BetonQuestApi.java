package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.quest.QuestTypeApi;

/**
 * The main API interface for BetonQuest, providing access to core functionalities.
 * This interface allows interaction with the quest system and features of BetonQuest.
 */
public interface BetonQuestApi {
    /**
     * Gets the QuestTypeAPI which provides access to the core quest logic.
     *
     * @return the QuestTypeAPI instance
     */
    QuestTypeApi getQuestTypeApi();

    /**
     * Gets the FeatureAPI which provides access to the BetonQuest features.
     *
     * @return the FeatureAPI instance
     */
    FeatureApi getFeatureApi();
}
