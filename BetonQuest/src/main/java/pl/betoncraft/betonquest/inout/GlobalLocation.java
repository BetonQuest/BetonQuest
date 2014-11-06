/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * 
 * @author Co0sh
 */
public class GlobalLocation {

	private Location location;
	private String[] conditions;
	private String[] events;
	private int distance;
	
	public GlobalLocation(String objective) {
		String instructions = ConfigInput.getString("objectives." + objective);
		if (instructions == null) {
			BetonQuest.getInstance().getLogger().severe("Global location not found: " + objective);
			return;
		}
		String[] parts = instructions.split(" ");
		String[] rawLocation = parts[1].split(";");
		location = new Location(Bukkit.getWorld(rawLocation[3]), Double.parseDouble(rawLocation[0]), Double.parseDouble(rawLocation[1]), Double.parseDouble(rawLocation[2]));
		for (String part : parts) {
			if (part.contains("conditions:")) {
				conditions = part.substring(11).split(",");
			}
			if (part.contains("events:")) {
				events = part.substring(7).split(",");
			}
		}
		distance = Integer.parseInt(rawLocation[4]);
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the conditions
	 */
	public String[] getConditions() {
		return conditions;
	}

	/**
	 * @return the events
	 */
	public String[] getEvents() {
		return events;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
}
