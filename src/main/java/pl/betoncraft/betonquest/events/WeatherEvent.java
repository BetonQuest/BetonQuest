/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.World;

import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class WeatherEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public WeatherEvent(String playerID, String instructions) {
		super(playerID, instructions);
		String weather = instructions.split(" ")[1];
		World world = PlayerConverter.getPlayer(playerID).getWorld();
		switch (weather) {
		case "sun":
			world.setThundering(false);
			world.setStorm(false);
			break;
		case "rain":
			world.setStorm(true);
			break;
		case "storm":
			world.setThundering(true);
			break;
		default:
			break;
		}
	}

}
