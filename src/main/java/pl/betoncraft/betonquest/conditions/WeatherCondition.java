/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.World;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class WeatherCondition extends Condition {
	
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
		}
		world = PlayerConverter.getPlayer(playerID).getWorld();
	}

	@Override
	public boolean isMet() {
		switch (weather) {
		case "sun":
			if (!world.isThundering() && !world.hasStorm()) {
				return true;
			}
			break;
		case "rain":
			if (world.hasStorm()) {
				return true;
			}
			break;
		case "storm":
			if (world.isThundering()) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

}
