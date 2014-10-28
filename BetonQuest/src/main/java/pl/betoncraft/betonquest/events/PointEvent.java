/**
 * 
 */
package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * 
 * @author Co0sh
 */
public class PointEvent extends QuestEvent {
	
	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public PointEvent(String playerID, String instructions) {
		super(playerID, instructions);
		if (!fire) {
			return;
		}
		String[] parts = instructions.split(" ");
		String category = parts[1];
		int count = Integer.valueOf(parts[2]);
		BetonQuest.getInstance().addPlayerPoints(playerID, category, count);
	}

	
}
