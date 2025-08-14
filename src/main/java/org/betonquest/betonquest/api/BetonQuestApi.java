package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.quest.QuestTypeApi;

/**
 * The main API interface for BetonQuest, providing access to core functionalities.
 * This interface allows interaction with the quest system and features of BetonQuest.
 */
public interface BetonQuestApi {
    /**
     * Gets the QuestTypeApi which provides access to the core quest logic.
     *
     * @return the Quest Type API instance
     */
    QuestTypeApi getQuestTypeApi();

    /**
     * Gets the FeatureApi which provides access to the BetonQuest features.
     *
     * @return the Feature API instance
     */
    FeatureApi getFeatureApi();
}
