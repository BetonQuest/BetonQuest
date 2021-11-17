package org.betonquest.betonquest;

import lombok.CustomLog;
import org.betonquest.betonquest.api.Objective;
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
@CustomLog
public class JoinQuitListener implements Listener {
    /**
     * Creates new listener, which will handle the data loading/saving
     */
    public JoinQuitListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        // if player was kicked, don't load the data
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        final String playerID = event.getUniqueId().toString();
        final BetonQuest plugin = BetonQuest.getInstance();
        plugin.putPlayerData(playerID, new PlayerData(playerID));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        // start objectives when the data is loaded
        PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
        // if the data still isn't loaded, force loading (this happens sometimes
        // probably because AsyncPlayerPreLoginEvent does not fire)
        if (playerData == null) {
            playerData = new PlayerData(playerID);
            BetonQuest.getInstance().putPlayerData(playerID, playerData);
            LOG.warning("Failed to load data for player " + event.getPlayer().getName() + ", forcing.");
        }
        playerData.startObjectives();
        GlobalObjectives.startAll(playerID);
        // display changelog message to the admins
        if (event.getPlayer().hasPermission("betonquest.admin")) {
            BetonQuest.getInstance().getUpdater().sendUpdateNotification(event.getPlayer());
            if (new File(BetonQuest.getInstance().getDataFolder(), "CHANGELOG.md").exists()) {
                try {
                    Config.sendNotify(null, PlayerConverter.getID(event.getPlayer()), "changelog", null, "changelog,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warning("The notify system was unable to play a sound for the 'changelog' category. Error was: '" + e.getMessage() + "'", e);
                }
            }
        }
        if (Journal.hasJournal(playerID)) {
            playerData.getJournal().update();
        }
        if (playerData.getConversation() != null) {
            new ConversationResumer(playerID, playerData.getConversation());
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        for (final Objective objective : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
            objective.pauseObjectiveForPlayer(playerID);
        }
        BetonQuest.getInstance().removePlayerData(playerID);
    }
}
