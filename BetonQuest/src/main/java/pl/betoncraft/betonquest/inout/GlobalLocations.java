/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.GlobalLocation;

/**
 * 
 * @author Co0sh
 */
public class GlobalLocations extends BukkitRunnable{
	
	private List<GlobalLocation> locations = new ArrayList<GlobalLocation>();
	private final List<GlobalLocation> finalLocations;

	public GlobalLocations() {
		String rawGlobalLocations = ConfigInput.getString("config.global_locations");
		if (rawGlobalLocations.equals("")) {
			finalLocations = null;
			return;
		}
		String[] parts = rawGlobalLocations.split(",");
		for (String objective : parts) {
			locations.add(new GlobalLocation(objective));
		}
		finalLocations = locations;
	}
	
	@Override
	public void run() {
		if (finalLocations == null) {
			this.cancel();
			return;
		}
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			locations:
			for (GlobalLocation location : finalLocations) {
				if (location.getLocation() == null) {
					this.cancel();
					return;
				}
				
				if (player.getLocation().getWorld().equals(location.getLocation().getWorld()) && player.getLocation().distance(new Location(Bukkit.getWorld(location.getLocation().getWorld()), location.getLocation().getX(), location.getLocation().getY(), location.getLocation().getZ())) < location.getDistance()) {
					if (location.getConditions() != null) {
						for (String condition : location.getConditions()) {
							if (!BetonQuest.condition(player.getName(), condition)) {
								continue locations;
							}
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
