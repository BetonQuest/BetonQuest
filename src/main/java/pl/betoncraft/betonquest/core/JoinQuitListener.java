/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.core;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.PlayerConverter.PlayerConversionType;

/**
 * Listener which handles data loadin/saving when players are joining/quitting
 *
 * @author Co0sh
 */
public class JoinQuitListener implements Listener {

    /**
     * BetonQuest's instance.
     */
    private BetonQuest instance = BetonQuest.getInstance();

    /**
     * Creates new listener, which will handle the data loading/saving
     */
    public JoinQuitListener() {
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void playerPreLogin(AsyncPlayerPreLoginEvent event) {
        // playerID is different when using UUID
        String playerID;
        if (PlayerConverter.getType() == PlayerConversionType.UUID) {
            playerID = event.getUniqueId().toString();
        } else {
            playerID = event.getName();
        }
        // kick the player if his data is already loaded
        BetonQuest plugin = BetonQuest.getInstance();
        if (plugin.getDBHandler(playerID) != null) {
            event.setLoginResult(Result.KICK_OTHER);
            return;
        }
        plugin.putDBHandler(playerID, new DatabaseHandler(playerID));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer());
        // start objectives when the data is loaded
        DatabaseHandler dbHandler = BetonQuest.getInstance().getDBHandler(playerID);
        // if the data still isn't loaded, force loading (this happens sometimes
        // probably because AsyncPlayerPreLoginEvent does not fire)
        if (dbHandler == null) {
            dbHandler = new DatabaseHandler(playerID);
            BetonQuest.getInstance().putDBHandler(playerID, dbHandler);
            Debug.error("Failed to load data for player " + playerID + ", forcing.");
        }
        dbHandler.startObjectives();
        // display changelog message to the admins
        if (event.getPlayer().hasPermission("betonquest.admin")
            && new File(BetonQuest.getInstance().getDataFolder(), "changelog.txt").exists()) {
            SimpleTextOutput.sendSystemMessage(
                    PlayerConverter.getID(event.getPlayer()),
                    Config.getMessage("changelog"),
                    Config.getString("config.sounds.update"));
        }
        if (Journal.hasJournal(playerID)) {
            dbHandler.getJournal().updateJournal();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        // save in async thread
        new BukkitRunnable() {
            @Override
            public void run() {
                DatabaseHandler dbHandler = BetonQuest.getInstance().getDBHandler(playerID);
                dbHandler.saveData();
                dbHandler.removeData();
                BetonQuest.getInstance().removeDBHandler(playerID);
            }
        }.runTaskAsynchronously(instance);
    }
}
