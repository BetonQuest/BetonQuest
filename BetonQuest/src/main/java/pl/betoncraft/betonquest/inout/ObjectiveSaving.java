/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 * @author Co0sh
 */
public class ObjectiveSaving implements Listener {

	private Objective objective;
	private String playerID;
	
	public ObjectiveSaving(String playerID, Objective objective) {
		this.objective = objective;
		this.playerID = playerID;
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	private void saveObjective(String playerID, String instructions) {
		BetonQuest.getInstance().getMySQL().openConnection();
		BetonQuest.getInstance().getMySQL().updateSQL("INSERT INTO objectives SET playerID='" + playerID + "', instructions='" + instructions + "'");
	}

	public void unregister() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getPlayer().equals(Bukkit.getPlayer(playerID))) {
			saveObjective(playerID, objective.getInstructions());
			HandlerList.unregisterAll(this);
		}
	}
}
