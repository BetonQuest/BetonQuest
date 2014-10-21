/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Conversation;

/**
 * 
 * @author Co0sh
 */
public class NPCListener implements Listener {
	
	public NPCListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		if (ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())) != null) {
			new Conversation(event.getClicker().getName(), ConfigInput.getString("npcs." + String.valueOf(event.getNPC().getId())), new NPCLocation(event.getNPC().getEntity().getLocation()));
		};
	}
}
