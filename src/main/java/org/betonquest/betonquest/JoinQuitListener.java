package org.betonquest.betonquest;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

/**
 * Listener which handles data loadin/saving when players are joining/quitting
 */
@SuppressWarnings("PMD.CommentRequired")
public class JoinQuitListener implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(JoinQuitListener.class);

    /**
     * Creates new listener, which will handle the data loading/saving
     */
    public JoinQuitListener() {
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
        if (playerData == null) {
            playerData = new PlayerData(onlineProfile);
            BetonQuest.getInstance().putPlayerData(onlineProfile, playerData);
            LOG.warn("Failed to load data for " + onlineProfile + ", forcing.");
        }
        playerData.startObjectives();
        GlobalObjectives.startAll(onlineProfile);
        // display changelog message to the admins
        if (event.getPlayer().hasPermission("betonquest.admin")) {
            BetonQuest.getInstance().getUpdater().sendUpdateNotification(event.getPlayer());
            if (new File(BetonQuest.getInstance().getDataFolder(), "CHANGELOG.md").exists()) {
                try {
                    Config.sendNotify(null, PlayerConverter.getID(event.getPlayer()), "changelog", null, "changelog,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warn("The notify system was unable to play a sound for the 'changelog' category. Error was: '" + e.getMessage() + "'", e);
                }
            }
        }

        if (Journal.hasJournal(onlineProfile)) {
            playerData.getJournal().update();
        }
        if (playerData.getConversation() != null) {
            new ConversationResumer(onlineProfile, playerData.getConversation());
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
