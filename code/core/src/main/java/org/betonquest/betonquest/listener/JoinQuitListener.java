package org.betonquest.betonquest.listener;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.quest.objective.resourcepack.ResourcepackObjective;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.Bukkit;
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
    private final ConversationApi conversationApi;

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
     * @param config            the plugin configuration file
     * @param questTypeApi      the object to get player Objectives
     * @param playerDataStorage the storage for un-/loading player data
     * @param conversationApi   the Conversation API
     * @param profileProvider   the profile provider instance
     * @param updater           the updater to notify players
     */
    public JoinQuitListener(final ConfigAccessor config,
                            final ObjectiveProcessor questTypeApi, final PlayerDataStorage playerDataStorage,
                            final ConversationApi conversationApi, final ProfileProvider profileProvider, final Updater updater) {
        this.config = config;
        this.questTypeApi = questTypeApi;
        this.playerDataStorage = playerDataStorage;
        this.conversationApi = conversationApi;
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
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        final Profile profile = profileProvider.getProfile(Bukkit.getOfflinePlayer(event.getUniqueId()));
        playerDataStorage.init(profile);
    }

    /**
     * Starts the player objectives and running conversation on join.
     *
     * @param event the join event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        final PlayerData playerData = playerDataStorage.get(onlineProfile);
        playerData.startObjectives();
        questTypeApi.startAll(onlineProfile, playerDataStorage);
        checkResourcepack(player, onlineProfile);

        if (Journal.hasJournal(onlineProfile)) {
            playerData.getJournal().update();
        }
        if (player.hasPermission("betonquest.admin")) {
            updater.sendUpdateNotification(player);
        }
        if (playerData.getActiveConversation() != null) {
            new ConversationResumer(config, conversationApi, onlineProfile, playerData.getActiveConversation());
        }
    }

    private void checkResourcepack(final Player player, final OnlineProfile onlineProfile) {
        final PlayerResourcePackStatusEvent.Status resourcePackStatus = player.getResourcePackStatus();
        if (resourcePackStatus != null) {
            questTypeApi.getActive(onlineProfile).stream()
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
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        for (final DefaultObjective objective : questTypeApi.getActive(onlineProfile)) {
            objective.pauseObjectiveForPlayer(onlineProfile);
        }
        playerDataStorage.remove(onlineProfile);
    }
}
