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
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * This class stores all active conversations. It should be used to prevent activating multiple conversations at once by all conversation starting things.
 * 
 * @author co0sh
 *
 */
public class ConversationContainer implements Listener {

	private static ArrayList<String> list = new ArrayList<>();
	
	public ConversationContainer() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	public static void addPlayer(String playerID) {
		list.add(playerID);
	}
	
	public static boolean containsPlayer(String playerID) {
		return list.contains(playerID);
	}
	
	public static void removePlayer(final String playerID) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				list.remove(playerID);
				
			}
		}.runTask(BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		list.remove(PlayerConverter.getID(event.getPlayer()));
	}
}
