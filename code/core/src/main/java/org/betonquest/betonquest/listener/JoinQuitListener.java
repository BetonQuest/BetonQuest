package org.betonquest.betonquest.listener;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.quest.objective.resourcepack.ResourcepackObjective;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * Listener which handles data loading/saving when players are joining/quitting.
 */
public class JoinQuitListener implements Listener {

    /**
     * A log source of the AsyncPlayerPreLoginEvent.
     */
    private static final LogSource ASYNC_JOIN_EVENT = () -> "AsyncPlayerPreLoginEvent";

    /**
     * Custom logger for debug messages.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin configuration file.
     */
    private final ConfigAccessor config;

    /**
     * Quest Type API.
     */
    private final ObjectiveProcessor questTypeApi;

    /**
     * Holds loaded PlayerData.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Updater to notify players.
     */
    private final Updater updater;

    /**
     * Creates new listener, which will handle the data loading/saving.
     *
     * @param log               the logger for debug messages
     * @param config            the plugin configuration file
     * @param questTypeApi      the object to get player Objectives
     * @param playerDataStorage the storage for un-/loading player data
     * @param conversations     the Conversation API
     * @param profileProvider   the profile provider instance
     * @param updater           the updater to notify players
     */
    public JoinQuitListener(final BetonQuestLogger log, final ConfigAccessor config,
                            final ObjectiveProcessor questTypeApi, final PlayerDataStorage playerDataStorage,
                            final Conversations conversations, final ProfileProvider profileProvider, final Updater updater) {
        this.log = log;
        this.config = config;
        this.questTypeApi = questTypeApi;
        this.playerDataStorage = playerDataStorage;
        this.conversations = conversations;
        this.profileProvider = profileProvider;
        this.updater = updater;
    }

    /**
     * Loads the player data async before it joins.
     *
     * @param event the async event to listen
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        log.debug(ASYNC_JOIN_EVENT, "Player '%s' with uuid '%s': '%s'".formatted(event.getName(), event.getUniqueId(), event.getLoginResult()));
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getUniqueId());
        log.debug(ASYNC_JOIN_EVENT, "Retrieve profile for offline player '%s'".formatted(offlinePlayer));
        final Profile profile = profileProvider.getProfile(offlinePlayer);
        log.debug(ASYNC_JOIN_EVENT, "Initializing player data async during pre-login for profile: '%s'".formatted(profile));
        playerDataStorage.init(profile);
        log.debug(ASYNC_JOIN_EVENT, "Player data async initialization completed for profile: '%s'".formatted(profile));
    }

    /**
     * Starts the player objectives and running conversation on join.
     *
     * @param event the join event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        log.debug("Player '%s' with uuid '%s' joined sync; initializing...".formatted(player.getName(), player.getUniqueId()));
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        log.debug("Profile '%s' obtained for player '%s' (online: %s)".formatted(onlineProfile, player.getName(), onlineProfile.getOnlineProfile().isPresent()));
        final PlayerData playerData = playerDataStorage.get(onlineProfile);
        log.debug("PlayerData obtained for player '%s'".formatted(player.getName()));
        questTypeApi.startAll(onlineProfile, playerDataStorage);
        checkResourcepack(player, onlineProfile);

        if (Journal.hasJournal(onlineProfile)) {
            playerData.getJournal().update();
            log.debug("Journal updated for player '%s'".formatted(player.getName()));
        }
        if (player.hasPermission("betonquest.admin")) {
            updater.sendUpdateNotification(player);
        }
        if (playerData.getActiveConversation() != null) {
            new ConversationResumer(config, conversations, onlineProfile, playerData.getActiveConversation());
            log.debug("Conversation resumed for player '%s'".formatted(player.getName()));
        }
    }

    private void checkResourcepack(final Player player, final OnlineProfile onlineProfile) {
        final PlayerResourcePackStatusEvent.Status resourcePackStatus = player.getResourcePackStatus();
        if (resourcePackStatus != null) {
            questTypeApi.getForProfile(onlineProfile).stream()
                    .filter(objective -> objective instanceof ResourcepackObjective)
                    .map(objective -> (ResourcepackObjective) objective)
                    .forEach(objective -> objective.getExceptionHandler()
                            .handle(() -> objective.processObjective(onlineProfile, resourcePackStatus)));
        }
    }

    /**
     * Removes the PlayerData from storage when the player quits the server.
     *
     * @param event the quit event
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        log.debug("Player '%s' with uuid '%s' quit; pausing objectives and removing from PlayerDataStorage...".formatted(player.getName(), player.getUniqueId()));
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        for (final Objective objective : questTypeApi.getForProfile(onlineProfile)) {
            questTypeApi.pause(onlineProfile, objective.getObjectiveID());
        }
        playerDataStorage.remove(onlineProfile);
    }
}
