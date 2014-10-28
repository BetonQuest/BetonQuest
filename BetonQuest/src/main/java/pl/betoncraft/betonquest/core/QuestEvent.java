/**
 * 
 */
package pl.betoncraft.betonquest.core;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * 
 * @author Co0sh
 */
public abstract class QuestEvent {
	
	protected String playerID;
	protected String instructions;
	protected boolean fire = true;

	public QuestEvent(String playerID, String instructions) {
		this.playerID = playerID;
		this.instructions = instructions;
		conditions:
		for (String part : instructions.split(" ")) {
			if (part.contains("conditions:")) {
				String[] conditions = part.substring(11).split(",");
				for (String condition : conditions) {
					if (!BetonQuest.condition(playerID, condition)) {
						fire = false;
						break conditions;
					}
				}
			}
		}
	}
}
