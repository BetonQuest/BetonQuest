package org.betonquest.betonquest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.objectives.ResourcePackObjective;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * Listener which handles data loadin/saving when players are joining/quitting
 */
@SuppressWarnings("PMD.CommentRequired")
public class JoinQuitListener implements Listener {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates new listener, which will handle the data loading/saving
     */
    public JoinQuitListener(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        final Profile profile = PlayerConverter.getID(Bukkit.getOfflinePlayer(event.getUniqueId()));
        final BetonQuest plugin = BetonQuest.getInstance();
        plugin.putPlayerData(profile, new PlayerData(profile));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        // start objectives when the data is loaded
        PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
        // if the data still isn't loaded, force loading (this happens sometimes
        // probably because AsyncPlayerPreLoginEvent does not fire)
        //noinspection ConstantValue
        if (playerData == null) {
            playerData = new PlayerData(onlineProfile);
            BetonQuest.getInstance().putPlayerData(onlineProfile, playerData);
            log.warn("Failed to load data for " + onlineProfile + ", forcing.");
        }
        playerData.startObjectives();
        GlobalObjectives.startAll(onlineProfile);
        final PlayerResourcePackStatusEvent.Status resourcePackStatus = event.getPlayer().getResourcePackStatus();
        if (resourcePackStatus != null) {
            BetonQuest.getInstance().getPlayerObjectives(onlineProfile).stream()
                    .filter(objective -> objective instanceof ResourcePackObjective)
                    .map(objective -> (ResourcePackObjective) objective)
                    .forEach(objective -> objective.processObjective(onlineProfile, resourcePackStatus));
        }

        if (Journal.hasJournal(onlineProfile)) {
            playerData.getJournal().update();
        }
        if (playerData.getActiveConversation() != null) {
            new ConversationResumer(loggerFactory, onlineProfile, playerData.getActiveConversation());
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        for (final Objective objective : BetonQuest.getInstance().getPlayerObjectives(onlineProfile)) {
            objective.pauseObjectiveForPlayer(onlineProfile);
        }
        BetonQuest.getInstance().removePlayerData(onlineProfile);
    }
}
