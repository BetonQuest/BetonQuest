/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Condition;

/**
 * 
 * @author Co0sh
 */
public class ConjunctionCondition extends Condition {

	private String[] conditions;
	
	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public ConjunctionCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("conditions:")) {
				conditions = part.substring(11).split(",");
			}
		}
	}

	@Override
	public boolean isMet() {
		for (String condition : conditions) {
			if (!BetonQuest.condition(playerID, condition)) {
				return false;
			}
		}
		return true;
	}
}
