/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.World;

import pl.betoncraft.betonquest.core.QuestEvent;

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
		World world = Bukkit.getPlayer(playerID).getWorld();
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
