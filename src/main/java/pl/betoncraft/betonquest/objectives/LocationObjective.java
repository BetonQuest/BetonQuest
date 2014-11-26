/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 * @author Co0sh
 */
public class LocationObjective extends Objective implements Listener {
	
	private Location location;
	private double distance;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public LocationObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String[] partsOfLoc = instructions.split(" ")[1].split(";");
		location = new Location(Bukkit.getWorld(partsOfLoc[3]), Double.valueOf(partsOfLoc[0]), Double.valueOf(partsOfLoc[1]), Double.valueOf(partsOfLoc[2]));
		distance = Double.valueOf(partsOfLoc[4]);
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (event.getPlayer().getName().equals(super.playerID) && event.getTo().distance(location) < distance && super.checkConditions()) {
			HandlerList.unregisterAll(this);
			super.completeObjective();
		}
	}
	
	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return instructions;
	}

}
