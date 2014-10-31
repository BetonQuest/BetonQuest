/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Condition;

/**
 * 
 * @author Co0sh
 */
public class LocationCondition extends Condition {
	
	private Location location;
	private double distance;
	boolean inverted;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public LocationCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] partsOfLoc = null;
		for (String part : instructions.split(" ")) {
			if (part.contains("loc:")) {
				partsOfLoc = part.substring(4).split(";");
			}
			if (part.equalsIgnoreCase("--inverted")) {
				inverted = true;
			}
		}
		if (partsOfLoc != null) {
			location = new Location(Bukkit.getWorld(partsOfLoc[3]), Double.valueOf(partsOfLoc[0]), Double.valueOf(partsOfLoc[1]), Double.valueOf(partsOfLoc[2]));
			distance = Double.valueOf(partsOfLoc[4]);
		}
	}

	@Override
	public boolean isMet() {
		if (location == null) {
			BetonQuest.getInstance().getLogger().severe("Location invalid at: " + instructions);
			return false;
		}
		if (Bukkit.getPlayer(playerID).getLocation().distance(location) <= distance) {
			return !inverted;
		}
		return inverted;
	}

}
