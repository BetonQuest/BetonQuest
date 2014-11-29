/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;

/**
 * 
 * @author Co0sh
 */
public class CitizensListener implements Listener {

	private static List<String> conversations = new ArrayList<String>();
	
	public CitizensListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	public static void removePlayerConversation(String playerID) {
		conversations.remove(playerID);
	}
	
	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		if (ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())) != null && !conversations.contains(PlayerConverter.getID(event.getClicker()))) {
			new Conversation(PlayerConverter.getID(event.getClicker()), ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())), new UnifiedLocation(event.getNPC().getEntity().getLocation()));
			conversations.add(PlayerConverter.getID(event.getClicker()));
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (conversations.contains(PlayerConverter.getID(event.getPlayer()))) {
			removePlayerConversation(PlayerConverter.getID(event.getPlayer()));
		}
	}
}
