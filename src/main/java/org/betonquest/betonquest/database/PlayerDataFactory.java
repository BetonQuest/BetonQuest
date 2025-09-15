package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;

/**
 * Factory to create PlayerData objects for profiles.
 */
public class PlayerDataFactory {
    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Saver to persist player data changes.
     */
    private final Saver saver;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new Player Data Factory.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param packManager   the quest package manager to get quest packages from
     * @param saver         the saver to persist data changes
     * @param questTypeApi  the Quest Type API
     */
    public PlayerDataFactory(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                             final Saver saver, final QuestTypeApi questTypeApi) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
        this.saver = saver;
        this.questTypeApi = questTypeApi;
    }

    /**
     * Create a new PlayerData.
     *
     * @param profile the profile to create the player data for
     * @return the newly created player data
     */
    public PlayerData createPlayerData(final Profile profile) {
        return new PlayerData(loggerFactory.create(PlayerData.class), packManager, saver, questTypeApi, profile);
    }
}
