/**
 * 
 */
package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.ConfigInput;

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
		if (!fire) {
			return;
		}
		BetonQuest.objective(playerID, ConfigInput.getString("objectives." + instructions.split(" ")[1]));
	}

}
