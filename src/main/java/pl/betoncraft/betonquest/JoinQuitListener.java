/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.conversation.ConversationResumer;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.io.File;
import java.util.logging.Level;

/**
 * Listener which handles data loadin/saving when players are joining/quitting
 *
 * @author Jakub Sapalski
 */
public class JoinQuitListener implements Listener {
    /**
     * Creates new listener, which will handle the data loading/saving
     */
    public JoinQuitListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPreLogin(AsyncPlayerPreLoginEvent event) {
        // if player was kicked, don't load the data
        if (event.getLoginResult() != Result.ALLOWED) {
            return;
        }
        String playerID = event.getUniqueId().toString();
        BetonQuest plugin = BetonQuest.getInstance();
        plugin.putPlayerData(playerID, new PlayerData(playerID));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer());
        // start objectives when the data is loaded
        PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
        // if the data still isn't loaded, force loading (this happens sometimes
        // probably because AsyncPlayerPreLoginEvent does not fire)
        if (playerData == null) {
            playerData = new PlayerData(playerID);
            BetonQuest.getInstance().putPlayerData(playerID, playerData);
            LogUtils.getLogger().log(Level.WARNING, "Failed to load data for player " + event.getPlayer().getName() + ", forcing.");
        }
        playerData.startObjectives();
        GlobalObjectives.startAll(playerID);
        // display changelog message to the admins
        if (event.getPlayer().hasPermission("betonquest.admin")
                && new File(BetonQuest.getInstance().getDataFolder(), "CHANGELOG.md").exists()) {
            Config.sendNotify(PlayerConverter.getID(event.getPlayer()), "changelog", null, "changelog,info");
        }
        if (Journal.hasJournal(playerID)) {
            playerData.getJournal().update();
        }
        if (playerData.getConversation() != null) {
            new ConversationResumer(playerID, playerData.getConversation());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer());
        for (Objective objective : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
            objective.removePlayer(playerID);
        }
        BetonQuest.getInstance().removePlayerData(playerID);
    }
}
