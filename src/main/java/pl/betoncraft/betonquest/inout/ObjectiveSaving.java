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
import pl.betoncraft.betonquest.database.UpdateType;

/**
 * 
 * @author Co0sh
 */
public class ObjectiveSaving implements Listener {

	private Objective objective;
	private String playerID;
	private String tag;
	
	/**
	 * Constructor method, this one safely removes objective from memory, storing it in database if needed
	 * @param playerID
	 * @param objective
	 */
	public ObjectiveSaving(String playerID, Objective objective) {
		this.objective = objective;
		this.playerID = playerID;
		this.tag = objective.getTag();
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
		BetonQuest.getInstance().putObjectiveSaving(this);
	}
	
	/**
	 * Saves objective to database and removes it from memory (in case of player's quit or server shutdown/reload)
	 */
	public void saveObjective() {
		BetonQuest.getInstance().getDB().updateSQL(UpdateType.ADD_NEW_OBJECTIVE, new String[]{playerID,objective.getInstructions()});
		deleteThis();
	}

	/**
	 * Deletes objective
	 */
	public void deleteThis() {
		objective.getInstructions();
		BetonQuest.getInstance().deleteObjectiveSaving(this);
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getPlayer().equals(PlayerConverter.getPlayer(playerID))) {
			if (BetonQuest.getInstance().isMySQLUsed()) {
				new BukkitRunnable() {
		            @Override
		            public void run() {
		            	BetonQuest.getInstance().getDB().openConnection();
		        		saveObjective();
		            }
		        }.runTaskAsynchronously(BetonQuest.getInstance());
			} else {
				BetonQuest.getInstance().getDB().openConnection();
				saveObjective();
				BetonQuest.getInstance().getDB().closeConnection();
			}
		}
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return the playerID
	 */
	public String getPlayerID() {
		return playerID;
	}
}
