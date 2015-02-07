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

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;

/**
 * This class stores all active conversations. It should be used to prevent activating
 * multiple conversations at once by all conversation starting things.
 * 
 * @author co0sh
 *
 */
public class ConversationContainer implements Listener {

	private static HashMap<String, Conversation> list = new HashMap<>();
	
	/**
	 * Creates a container for players' conversations.
	 */
	public ConversationContainer() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	/**
	 * Adds the player to the list of active conversations
	 * @param playerID - ID of the player
	 * @param conversation - pointer to player's conversation
	 */
	public static void addPlayer(String playerID, Conversation conversation) {
		list.put(playerID, conversation);
	}
	
	/**
	 * Checks if the player is in a conversation
	 * @param playerID
	 * @return if the player is on the list of active conversations
	 */
	public static boolean containsPlayer(String playerID) {
		return list.containsKey(playerID);
	}
	
	/**
	 * Removes player from the list of active conversations. This does not end the conversation!
	 * @param playerID - ID of the player
	 */
	public static void removePlayer(final String playerID) {
		list.remove(playerID);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		list.remove(PlayerConverter.getID(event.getPlayer()));
	}
	
	/**
	 * Ends every active conversation for every online player
	 */
	public static void clear() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			String playerID = PlayerConverter.getID(player);
			if (list.containsKey(playerID))
				list.get(playerID).endConversation();
		}
	}
	
	/**
	 * Gets this player's active conversation.
	 * @param playerID - ID of the player
	 * @return player's active conversation or null if there is no conversation
	 */
	public static Conversation getConversation(String playerID) {
		return list.get(playerID);
	}
}
