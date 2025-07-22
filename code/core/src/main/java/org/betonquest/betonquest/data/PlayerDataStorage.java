package org.betonquest.betonquest.data;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileKeyMap;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores loaded {@link PlayerData}.
 */
public class PlayerDataStorage {
    /**
     * LoggerFactory to create new custom logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom logger for debug messages.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin configuration file.
     */
    private final ConfigAccessor config;

    /**
     * Objective processor to start (global) objectives.
     */
    private final ObjectiveProcessor objectives;

    /**
     * Stored player data for online players.
     */
    private final Map<Profile, PlayerData> playerDataMap;

    /**
     * Create a new Storage for Player Data.
     *
     * @param loggerFactory   the logger factory to use in Conversation Resumer
     * @param log             the logger for debug messages
     * @param config          the plugin configuration file
     * @param objectives      the objective processor to start (global) objectives
     * @param profileProvider the profile provider to use
     */
    public PlayerDataStorage(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                             final ConfigAccessor config, final ObjectiveProcessor objectives,
                             final ProfileProvider profileProvider) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.config = config;
        this.objectives = objectives;
        this.playerDataMap = new ProfileKeyMap<>(profileProvider, new ConcurrentHashMap<>());
    }

    /**
     * Creates PlayerData for the online profiles, stores them and starts their objectives.
     *
     * @param onlineProfiles the profiles to initialize
     * @param pluginMessage  the plugin message for journals and conversations
     */
    public void initProfiles(final Collection<OnlineProfile> onlineProfiles, final PluginMessage pluginMessage) {
        for (final OnlineProfile onlineProfile : onlineProfiles) {
            final PlayerData playerData = new PlayerData(onlineProfile);
            put(onlineProfile, playerData);
            playerData.startObjectives();
            playerData.getJournal(pluginMessage).update();
            if (playerData.getActiveConversation() != null) {
                new ConversationResumer(loggerFactory, config, pluginMessage, onlineProfile, playerData.getActiveConversation());
            }
        }
    }

    /**
     * Start the objectives for all stored player data.
     */
    public void startObjectives() {
        for (final PlayerData playerData : playerDataMap.values()) {
            playerData.startObjectives();
        }
    }

    /**
     * Start global objectives and update journals.
     *
     * @param onlineProfiles the profiles to update
     * @param pluginMessage  the plugin message to generate new journals
     */
    public void reloadProfiles(final Collection<OnlineProfile> onlineProfiles, final PluginMessage pluginMessage) {
        for (final OnlineProfile onlineProfile : onlineProfiles) {
            log.debug("Updating global objectives and journal for player " + onlineProfile);
            final PlayerData playerData = get(onlineProfile);
            objectives.startAll(onlineProfile, this);
            playerData.getJournal(pluginMessage).update();
        }
    }

    /**
     * Stores the PlayerData in a map, so it can be retrieved using
     * {@link #get(Profile profile)}.
     *
     * @param profile    the {@link Profile} of the player
     * @param playerData PlayerData object to store
     */
    public void put(final Profile profile, final PlayerData playerData) {
        log.debug("Inserting data for " + profile);
        playerDataMap.put(profile, playerData);
    }

    /**
     * Retrieves PlayerData object for specified profile. If the playerData does
     * not exist it will create new playerData and store it.
     *
     * @param profile the {@link OnlineProfile} of the player
     * @return PlayerData object for the player
     */
    public PlayerData get(final OnlineProfile profile) {
        return get((Profile) profile);
    }

    /**
     * Retrieves PlayerData object for specified profile. If the playerData does
     * not exist but the profile is online, it will create new playerData and store it.
     *
     * @param profile the {@link Profile} of the player
     * @return PlayerData object for the player
     * @throws IllegalArgumentException when there is no data and the player is offline
     */
    public PlayerData get(final Profile profile) {
        PlayerData playerData = playerDataMap.get(profile);
        if (playerData == null) {
            if (profile.getOnlineProfile().isPresent()) {
                playerData = new PlayerData(profile);
                put(profile, playerData);
            } else {
                throw new IllegalArgumentException("The profile has no online player!");
            }
        }
        return playerData;
    }

    /**
     * Retrieves PlayerData object for specified profile. If the playerData does
     * not exist it will create new playerData but won't store it.
     *
     * @param profile the {@link Profile} of the player
     * @return PlayerData object for the player
     * @throws IllegalArgumentException when there is no data and the player is offline
     */
    public PlayerData getOffline(final Profile profile) {
        if (profile.getOnlineProfile().isPresent()) {
            return get(profile);
        }
        return new PlayerData(profile);
    }

    /**
     * Removes the database playerData from the map.
     *
     * @param profile the {@link Profile} of the player whose playerData is to be removed
     */
    public void remove(final Profile profile) {
        playerDataMap.remove(profile);
    }
}
