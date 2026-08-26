package org.betonquest.betonquest.data;

import dev.faststats.data.Metric;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.faststats.FastStatsMetricsProvider;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Stores loaded {@link PlayerData}.
 */
public class PlayerDataStorage implements FastStatsMetricsProvider {

    /**
     * Custom logger for debug messages.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin configuration file.
     */
    private final ConfigAccessor config;

    /**
     * Factory to create new Player Data.
     */
    private final PlayerDataFactory playerDataFactory;

    /**
     * Objective processor to start (auto-once) objectives.
     */
    private final ObjectiveProcessor objectives;

    /**
     * Stored player data for online players.
     */
    private final Map<Profile, FutureTask<PlayerData>> playerDataMap;

    /**
     * Create a new Storage for Player Data.
     *
     * @param log               the logger for debug messages
     * @param config            the plugin configuration file
     * @param playerDataFactory the factory to create player data
     * @param objectives        the objective processor to start (auto-once) objectives
     * @param profileProvider   the profile provider to use
     */
    public PlayerDataStorage(final BetonQuestLogger log,
                             final ConfigAccessor config, final PlayerDataFactory playerDataFactory, final ObjectiveProcessor objectives,
                             final ProfileProvider profileProvider) {
        this.log = log;
        this.config = config;
        this.playerDataFactory = playerDataFactory;
        this.objectives = objectives;
        this.playerDataMap = new ProfileKeyMap<>(profileProvider, new ConcurrentHashMap<>());
    }

    /**
     * Creates PlayerData for the online profiles, stores them and starts their objectives.
     *
     * @param onlineProfiles the profiles to initialize
     * @param conversations  the Conversation API
     */
    public void initProfiles(final Collection<OnlineProfile> onlineProfiles, final Conversations conversations) {
        log.debug("Initializing profiles and starting objectives for %d online profile(s)...".formatted(onlineProfiles.size()));
        for (final OnlineProfile onlineProfile : onlineProfiles) {
            final PlayerData playerData = get(onlineProfile);
            playerData.startObjectives();
            playerData.getJournal().update();
            if (playerData.getActiveConversation() != null) {
                new ConversationResumer(config, conversations, onlineProfile, playerData.getActiveConversation());
            }
        }
    }

    /**
     * Start objectives, auto-once objectives and update journals.
     *
     * @param onlineProfiles the profiles to update
     */
    public void reloadProfiles(final Collection<OnlineProfile> onlineProfiles) {
        log.debug("Reloading %d online profile(s)...".formatted(onlineProfiles.size()));
        for (final OnlineProfile onlineProfile : onlineProfiles) {
            final PlayerData playerData = get(onlineProfile);
            objectives.startAll(onlineProfile, this);
            playerData.getJournal().update();
        }
    }

    /**
     * Creates new PlayerData and stores it.
     *
     * @param profile the {@link Profile} of the player
     * @return the created PlayerData
     */
    public PlayerData init(final Profile profile) {
        log.debug("Initializing PlayerData for profile: %s".formatted(profile));
        final FutureTask<PlayerData> playerDataFutureTask = playerDataMap.compute(profile, (key, task) -> {
            if (task == null || task.isDone()) {
                final FutureTask<PlayerData> newTask = new FutureTask<>(() -> playerDataFactory.createPlayerData(key));
                newTask.run();
                return newTask;
            }
            return task;
        });
        return saveGet(playerDataFutureTask);
    }

    /**
     * Retrieves PlayerData object for the specified profile. If the playerData does
     * not exist, it will create a new playerData.
     * If the player is online, it will be stored as well.
     *
     * @param profile the {@link Profile} of the player
     * @return PlayerData object for the player
     */
    public PlayerData get(final Profile profile) {
        log.debug("Getting PlayerData for %s (cached=%s, online=%s)".formatted(profile, playerDataMap.containsKey(profile), profile.getOnlineProfile().isPresent()));
        final FutureTask<PlayerData> playerData = playerDataMap.get(profile);
        if (playerData != null) {
            return saveGet(playerData);
        }
        if (profile.getOnlineProfile().isPresent()) {
            return init(profile);
        }
        return playerDataFactory.createPlayerData(profile);
    }

    private PlayerData saveGet(final FutureTask<PlayerData> playerData) {
        try {
            return playerData.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Failed to load profile data!", e);
        }
    }

    /**
     * Removes the database playerData from the map.
     *
     * @param profile the {@link Profile} of the player whose playerData is to be removed
     */
    public void remove(final Profile profile) {
        log.debug("Removing PlayerData from storage for %s".formatted(profile));
        playerDataMap.remove(profile);
    }

    @Override
    public Set<Metric<?>> getMetrics() {
        return Set.of(
                Metric.number("profiles_personal_lang_count", () -> playerDataMap.values().stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (InterruptedException | ExecutionException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .filter(data -> data.getLanguage().isPresent()
                                && !"default".equalsIgnoreCase(data.getLanguage().get())).count()),
                Metric.stringArray("profiles_personal_lang", () -> playerDataMap.values().stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (InterruptedException | ExecutionException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .map(data -> data.getLanguage().orElse(null)).filter(Objects::nonNull)
                        .filter(lang -> !"default".equalsIgnoreCase(lang))
                        .toList().toArray(new String[0]))
        );
    }
}
