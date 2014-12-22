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
public class TagEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public TagEvent(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		switch (parts[1]) {
		case "add":
			for (String tag : parts[2].split(",")) {
				BetonQuest.getInstance().putPlayerTag(playerID, tag);
			}
			break;
		default:
			for (String tag : parts[2].split(",")) {
				BetonQuest.getInstance().removePlayerTag(playerID, tag);
			}
			break;
		}
	}
	
	
}
