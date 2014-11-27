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
public class TimeEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public TimeEvent(String playerID, String instructions) {
		super(playerID, instructions);
		World world = PlayerConverter.getPlayer(playerID).getWorld();
		String string = instructions.split(" ")[1];
		long time;
		if (string.matches("/+//d+")) {
			time = world.getTime() + Long.valueOf(string) * 1000 + 18000;
		} else {
			time = Long.valueOf(string) * 1000 + 18000;
		}
		world.setTime(time % 24000);
	}

}
