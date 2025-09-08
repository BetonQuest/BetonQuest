package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.feature.FeatureRegistries;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;

/**
 * The main API interface for BetonQuest, providing access to core functionalities.
 * This interface allows interaction with the quest system and features of BetonQuest.
 */
public interface BetonQuestApi {
    /**
     * Gets the {@link QuestPackageManager} which provides access to the
     * {@link org.betonquest.betonquest.api.config.quest.QuestPackage}s.
     *
     * @return the Quest Package Manager instance
     */
    QuestPackageManager getQuestPackageManager();

    /**
     * Gets the core QuestTypeRegistries to access and add new core implementations.
     *
     * @return registries for core types
     */
    QuestTypeRegistries getQuestRegistries();

    /**
     * Gets the QuestTypeApi which provides access to the core quest logic.
     *
     * @return the Quest Type API instance
     */
    QuestTypeApi getQuestTypeApi();

    /**
     * Gets the FeatureRegistries to access and add new feature implementations.
     *
     * @return registries for feature types
     */
    FeatureRegistries getFeatureRegistries();

    /**
     * Gets the FeatureApi which provides access to the BetonQuest features.
     *
     * @return the Feature API instance
     */
    FeatureApi getFeatureApi();
}
