package org.betonquest.betonquest.data;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.GlobalObjectives;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ActiveProfileChangedEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores loaded {@link PlayerData}.
 */
public class PlayerDataStorage implements Listener {
    /**
     * LoggerFactory to create new custom logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom logger for debug messages.
     */
    private final BetonQuestLogger log;

    /**
     * Stored player data for online players.
     */
    private final Map<Profile, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    /**
     * Create a new Storage for Player Data.
     *
     * @param loggerFactory the logger factory to use in Conversation Resumer
     * @param log           the logger for debug messages
     */
    public PlayerDataStorage(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onActiveProfileChanged(final ActiveProfileChangedEvent event) {
        final OnlineProfile newProfile = event.getNewProfile();
        final PlayerData playerData = get(newProfile);
        remove(event.getOldProfile());
        put(newProfile, playerData);
        playerData.startObjectives(); 
        playerData.getJournal().update();
        if (playerData.getActiveConversation() != null) {
            new ConversationResumer(loggerFactory, newProfile, playerData.getActiveConversation());
        }
    }

    /**
     * Creates PlayerData for the online profiles, stores them and starts their objectives.
     *
     * @param onlineProfiles the profiles to initialize
     */
    public void initProfiles(final Collection<OnlineProfile> onlineProfiles) {
        for (final OnlineProfile onlineProfile : onlineProfiles) {
            final PlayerData playerData = new PlayerData(onlineProfile);
            put(onlineProfile, playerData);
            playerData.startObjectives();
            playerData.getJournal().update();
            if (playerData.getActiveConversation() != null) {
                new ConversationResumer(loggerFactory, onlineProfile, playerData.getActiveConversation());
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
     */
    public void reloadProfiles(final Collection<OnlineProfile> onlineProfiles) {
        for (final OnlineProfile onlineProfile : onlineProfiles) {
            log.debug("Updating global objectives and journal for player " + onlineProfile);
            final PlayerData playerData = get(onlineProfile);
            GlobalObjectives.startAll(onlineProfile, this);
            playerData.getJournal().update();
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
        BetonQuest.getInstance().getLogger().info("get: " + profile.getProfileName() + " " + profile.getProfileUUID() + " " + profile.getPlayer().getName());
        PlayerData playerData = playerDataMap.get(profile);
        BetonQuest.getInstance().getLogger().info("playerData loaded: " + playerData);
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
