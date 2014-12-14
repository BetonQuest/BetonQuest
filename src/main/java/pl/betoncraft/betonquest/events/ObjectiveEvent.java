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
public class ObjectiveEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public ObjectiveEvent(String playerID, String instructions) {
		super(playerID, instructions);
		BetonQuest.objective(playerID, instructions.substring(instructions.indexOf(" ") + 1));
	}

}
