/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 * @author Co0sh
 */
public class ObjectiveSaving implements Listener {

	private Objective objective;
	private String playerID;
	
	/**
	 * Constructor method, this one safely removes objective from memory, storing it in database if needed
	 * @param playerID
	 * @param objective
	 */
	public ObjectiveSaving(String playerID, Objective objective) {
		this.objective = objective;
		this.playerID = playerID;
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
		BetonQuest.getInstance().putObjectiveSaving(this);
	}
	
	/**
	 * Saves objective to database and removes it from memory (in case of player's quit or server shutdown/reload)
	 */
	public void saveObjective() {
		BetonQuest.getInstance().getMySQL().updateSQL("INSERT INTO objectives SET playerID='" + playerID + "', instructions='" + objective.getInstructions() + "', isused='0'");
		deleteThis();
	}

	/**
	 * Deleted objective (in case of it's completion)
	 */
	public void unregister() {
		deleteThis();
	}

	/**
	 * Deletes objective
	 */
	private void deleteThis() {
		BetonQuest.getInstance().deleteObjectiveSaving(this);
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getPlayer().equals(Bukkit.getPlayer(playerID))) {
			new BukkitRunnable() {
	            @Override
	            public void run() {
	        		saveObjective();
	            }
	        }.runTaskAsynchronously(BetonQuest.getInstance());
		}
	}
}
