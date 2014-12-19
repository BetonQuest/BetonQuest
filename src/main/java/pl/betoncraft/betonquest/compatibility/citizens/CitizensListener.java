/**
 * 
 */
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.ConversationContainer;
import pl.betoncraft.betonquest.inout.PlayerConverter;
import pl.betoncraft.betonquest.inout.UnifiedLocation;

/**
 * 
 * @author Co0sh
 */
public class CitizensListener implements Listener {
	
	public CitizensListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		if (ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())) != null && !ConversationContainer.containsPlayer(PlayerConverter.getID(event.getClicker())) && event.getClicker().hasPermission("betonquest.conversation")) {
			new Conversation(PlayerConverter.getID(event.getClicker()), ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())), new UnifiedLocation(event.getNPC().getEntity().getLocation()));
			ConversationContainer.addPlayer(PlayerConverter.getID(event.getClicker()));
		}
	}
}
