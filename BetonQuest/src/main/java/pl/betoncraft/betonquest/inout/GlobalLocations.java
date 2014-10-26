/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * 
 * @author Co0sh
 */
public class GlobalLocations extends BukkitRunnable{
	
	private List<GlobalLocation> locations = new ArrayList<GlobalLocation>();
	private final List<GlobalLocation> finalLocations;

	public GlobalLocations() {
		String[] parts = ConfigInput.getString("config.global_locations").split(",");
		for (String objective : parts) {
			locations.add(new GlobalLocation(objective));
		}
		finalLocations = locations;
	}
	
	@Override
	public void run() {
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			locations:
			for (GlobalLocation location : finalLocations) {
				if (player.getLocation().distance(location.getLocation()) < location.getDistance()) {
					for (String condition : location.getConditions()) {
						if (!BetonQuest.condition(player.getName(), condition)) {
							continue locations;
						}
					}
					for (String event : location.getEvents()) {
						BetonQuest.event(player.getName(), event);
					}
				}
			}
		}
	}
}
