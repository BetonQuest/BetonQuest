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
public class DeleteObjectiveEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public DeleteObjectiveEvent(String playerID, String instructions) {
		super(playerID, instructions);
		if (!fire) {
			return;
		}
		BetonQuest.getInstance().deleteObjective(playerID, instructions.split(" ")[1]);
	}

}
