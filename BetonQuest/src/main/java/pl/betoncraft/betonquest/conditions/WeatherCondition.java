/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;
import org.bukkit.World;

import pl.betoncraft.betonquest.core.Condition;

/**
 * 
 * @author Co0sh
 */
public class WeatherCondition extends Condition {
	
	private boolean inverted = false;
	private String weather;
	private World world;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public WeatherCondition(String playerID, String instructions) {
		super(playerID, instructions);
		for (String part : instructions.split(" ")) {
			if (part.contains("type:")) {
				weather = part.substring(5);
			}
			if (part.equals("--inverted")) {
				inverted = true;
			}
		}
		world = Bukkit.getPlayer(playerID).getWorld();
	}

	@Override
	public boolean isMet() {
		switch (weather) {
		case "sun":
			if (!world.isThundering() && !world.hasStorm()) {
				return !inverted;
			}
			break;
		case "rain":
			if (world.hasStorm()) {
				return !inverted;
			}
			break;
		case "storm":
			if (world.isThundering()) {
				return !inverted;
			}
			break;
		default:
			break;
		}
		return inverted;
	}

}
